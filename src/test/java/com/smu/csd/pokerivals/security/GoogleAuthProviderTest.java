package com.smu.csd.pokerivals.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.smu.csd.pokerivals.persistence.entity.user.Player;
import com.smu.csd.pokerivals.persistence.repository.UserRepository;
import com.smu.csd.pokerivals.security.authentication.CompletedGoogleAuthentication;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthProviderTest {
    // taken from jwt.io
    String sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6ImpvaG4uZG9lQHNtdS5lZHUuc2cifQ.bTSJrbj-HRCavDhEF4AH9TK4pOh3uImqnkYwmEPxn78";
    String sampleSub = "1234567890";

    @Test
    public void getDecoded_normalJWT_success() {

        DecodedToken decodedToken = DecodedToken.getDecoded(sampleToken);

        assertEquals("John Doe", decodedToken.getName());
        assertEquals(sampleSub, decodedToken.getSub());
        assertEquals("john.doe@smu.edu.sg", decodedToken.getEmail());
    }

    @Mock
    private GoogleIdTokenVerifier verifier;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private GoogleAuthenticationProvider authProvider;

    @Test
    public void authenticate_existingUser_success() throws GeneralSecurityException, IOException {
        // when
        GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock (GoogleIdToken.Payload.class);

        when(verifier.verify(sampleToken)).thenReturn(idTokenMock);
        when(idTokenMock.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn("1234567890");

        when(userRepo.findOneByGoogleSub(sampleSub)).thenReturn(Optional.of(new Player("johndoe",sampleSub)));

        IncompleteGoogleAuthentication authenticationBefore = new IncompleteGoogleAuthentication(sampleToken);
        Authentication authenticationAfter = authProvider.authenticate(authenticationBefore);

        assertInstanceOf(CompletedGoogleAuthentication.class, authenticationAfter);
        assertEquals("John Doe", authenticationBefore.getName());
        assertEquals("johndoe", authenticationAfter.getName()); // become username!
        assertInstanceOf(UserDetails.class, authenticationAfter.getPrincipal());
        assertEquals( ((UserDetails) authenticationAfter.getPrincipal()).getUsername(), "johndoe");

        verify(verifier).verify(sampleToken);
        verify(idTokenMock).getPayload();
        verify(payload).getSubject();
        verify(userRepo).findOneByGoogleSub(sampleSub);

    }


    @Test
    public void authenticate_nonExistingUser_success() throws  GeneralSecurityException, IOException {
        // when
        GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock (GoogleIdToken.Payload.class);

        when(verifier.verify(sampleToken)).thenReturn(idTokenMock);
        when(idTokenMock.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn("1234567890");

        when(userRepo.findOneByGoogleSub(sampleSub)).thenReturn(Optional.empty());

        IncompleteGoogleAuthentication authenticationBefore = new IncompleteGoogleAuthentication(sampleToken);

        Exception exception = assertThrows(UsernameNotFoundException.class, ()-> authProvider.authenticate(authenticationBefore));
        assertEquals("User not found", exception.getMessage());

        verify(verifier).verify(sampleToken);
        verify(idTokenMock).getPayload();
        verify(payload).getSubject();
        verify(userRepo).findOneByGoogleSub(sampleSub);

    }
}
