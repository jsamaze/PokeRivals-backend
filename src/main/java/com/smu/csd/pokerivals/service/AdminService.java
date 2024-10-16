package com.smu.csd.pokerivals.service;

import com.smu.csd.pokerivals.exception.MacInvalidException;
import com.smu.csd.pokerivals.persistence.entity.user.Admin;
import com.smu.csd.pokerivals.persistence.repository.AdminRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.MacAlgorithmSpec;
import software.amazon.awssdk.services.kms.model.VerifyMacResponse;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AdminService {
    private AdminRepository adminRepo;
    private KmsClient kmsClient;
    private LambdaAsyncClient lambdaClient;
    private GoogleIdTokenVerifier verifier;

    @Value("${email.linkaccount.validityseconds}")
    private Integer validitySeconds;

    @Value("${email.linkaccount.kmskeyid}")
    private String kmsKeyId;


    @Value("${email.linkaccount.lambdaARN}")
    private String lambdaArn;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
     public AdminService(AdminRepository repo, KmsClient kmsClient, LambdaAsyncClient lambdaClient, GoogleIdTokenVerifier verifier)  {
         this.adminRepo = repo;
         this.kmsClient = kmsClient;
         this.lambdaClient = lambdaClient;
         this.verifier = verifier;
     }

     @RolesAllowed("ROLE_ADMIN")
     @Transactional
     public void register(String inviterUsername, Admin admin) throws JsonProcessingException, ExecutionException, InterruptedException{
         admin = adminRepo.save(admin);

         Admin inviter = adminRepo.findById(inviterUsername).orElseThrow();
         inviter.addInvitee(admin);
         adminRepo.save(inviter);

         sendLinkEmail(admin.getUsername());
     }

     private static record LambdaEmailDTO(String username, String email){};

     @RolesAllowed("ROLE_ADMIN")
     public void sendLinkEmail(String username) throws JsonProcessingException, ExecutionException, InterruptedException {
        Admin admin = adminRepo.findById(username).orElseThrow(()-> new NoSuchElementException("Admin does not exist!"));

         byte[] json = jacksonObjectMapper.writeValueAsBytes(new LambdaEmailDTO(admin.getUsername(), admin.getEmail()));

         CompletableFuture<InvokeResponse> future = lambdaClient.invoke(b -> {
             b.functionName(lambdaArn)
                     .invocationType(InvocationType.EVENT)
                     .payload(SdkBytes.fromByteArray(
                                json
                             ));
         });

         InvokeResponse response = future.get();
     }

    @RolesAllowed("ROLE_ADMIN")
    public List<Admin> getInvitees(String adminUsername) {
        return adminRepo.findAdminsInvitedBy(adminUsername);
    }

     @Getter
     public static class LinkAccountDTO {
        @Getter
        private String username;
        private String email;
        private long time;
        private String mac;

        @Getter
        private String credentials;

        public boolean checkValidity(long validitySeconds){
            return ((System.currentTimeMillis() / 1000L) - time ) < validitySeconds;
        }

        public boolean checkMac(KmsClient client, String keyId){
            VerifyMacResponse verifyMacResponse = client.verifyMac( r -> {
                r.keyId(keyId)
                        .mac(SdkBytes.fromByteArray(
                                Base64.getUrlDecoder().decode(mac)
                        ))
                        .macAlgorithm(MacAlgorithmSpec.HMAC_SHA_512)
                        .message(SdkBytes.fromByteArray(
                                (username + email + Long.toString(time)).getBytes()
                        ));
            });

            return verifyMacResponse.macValid();
        }


         public Date getTime(){
            return new Date(time * 1000L);
         }

     }

     public void linkEmail(LinkAccountDTO dto){
        if (! (dto.checkValidity(validitySeconds) && dto.checkMac(kmsClient, kmsKeyId) )){
            throw new MacInvalidException();
        }

         Admin admin = adminRepo.findById(dto.getUsername()).orElseThrow(()-> new NoSuchElementException("Admin does not exist!"));

         try {
             GoogleIdToken idToken = verifier.verify( dto.getCredentials());
             if (idToken != null) {
                 GoogleIdToken.Payload payload = idToken.getPayload();

                 String userId = payload.getSubject();
                 admin.updateGoogleSub(dto.getTime(),userId);

                 adminRepo.save(admin);

             } else {
                 throw new BadCredentialsException("Expired token");
             }
         } catch (GeneralSecurityException e){
             throw new AuthenticationException("General Security Exception"){};
         } catch (IOException e){
             throw new AuthenticationException("IOException"){};
         }
     }
}
