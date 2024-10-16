package com.smu.csd.pokerivals.service;

import com.smu.csd.pokerivals.persistence.entity.user.*;
import com.smu.csd.pokerivals.persistence.repository.PlayerRepository;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@Slf4j
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GoogleIdTokenVerifier verifier;

    @Autowired
    public PlayerService(PlayerRepository repo, GoogleIdTokenVerifier verifier){
        this.playerRepository= repo;
        this.verifier = verifier;
    }

    // not transactional
    public void register(Player player, IncompleteGoogleAuthentication token){
        try {
            GoogleIdToken idToken = verifier.verify((String) token.getCredentials());
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                log.trace("Verified as" + payload.get("name"));

                player.updateGoogleSub(new Date(System.currentTimeMillis()),userId);
                player.setEmail((String) payload.get("email"));
                player = playerRepository.save(player);

            } else {
                log.info("Expired token");
                throw new BadCredentialsException("Expired token");
            }
        } catch (GeneralSecurityException e){
            throw new AuthenticationException("General Security Exception"){};
        } catch (IOException e){
            throw new AuthenticationException("IOException"){};
        }

    }

}
