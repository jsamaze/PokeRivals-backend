package com.smu.csd.pokerivals.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.smu.csd.pokerivals.persistence.entity.user.User;
import com.smu.csd.pokerivals.persistence.repository.UserRepository;
import com.smu.csd.pokerivals.security.authentication.CompletedGoogleAuthentication;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoogleAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleIdTokenVerifier verifier;

    @Override
    @SneakyThrows
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (authentication instanceof IncompleteGoogleAuthentication token){
                GoogleIdToken idToken = verifier.verify((String) token.getCredentials()); // checked exception here are swallowed
                if (idToken != null) {
                    GoogleIdToken.Payload payload = idToken.getPayload();

                    // Print user identifier
                    String userId = payload.getSubject();
                    log.trace("Verified as" + payload.get("name"));

                    User user = userRepository.findOneByGoogleSub(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));

                    return new CompletedGoogleAuthentication(user);
                } else {
                    log.info("Expired token");
                    throw new BadCredentialsException("Expired token");
                }
        } else {
            throw new AuthenticationException("Error with authentication"){};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(IncompleteGoogleAuthentication.class);
    }
}