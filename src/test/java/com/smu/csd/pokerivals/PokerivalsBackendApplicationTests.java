package com.smu.csd.pokerivals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.net.URI;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.smu.csd.pokerivals.security.GoogleAuthenticationProvider;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import com.smu.csd.pokerivals.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import lombok.Setter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/** Start an actual HTTP server listening at a random port */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PokerivalsBackendApplicationTests {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://127.0.0.1:";

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private GoogleIdTokenVerifier verifier;

	// This test depends on a third party, thus we used mocking
	@Test
	public void login_Success() throws Exception {
		// Arrange
		record LoginDetails (
			 String credentials
		){};

		URI uri = new URI(baseUrl + port + "/auth/login");

		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6ImpvaG4uZG9lQHNtdS5lZHUuc2cifQ.bTSJrbj-HRCavDhEF4AH9TK4pOh3uImqnkYwmEPxn78";

		GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
		GoogleIdToken.Payload payload = mock (GoogleIdToken.Payload.class);

		when(verifier.verify(token)).thenReturn(idTokenMock);
		when(idTokenMock.getPayload()).thenReturn(payload);
		when(payload.getSubject()).thenReturn("101754849930742817639");

		LoginDetails loginDetails1 = new LoginDetails(token);

		// Act
		ResponseEntity<String> result = restTemplate.postForEntity(uri, loginDetails1, String.class);

		// Assert
		assertEquals(200, result.getStatusCode().value());
		assertNotNull(result);
		assertNotNull(result.getHeaders().get("Set-Cookie"));
		verify(verifier).verify(token);
		verify(idTokenMock).getPayload();
	}

	@Test
	public void login_Failure() throws Exception {
		// Arrange
		record LoginDetails (
				String credentials
		){};

		URI uri = new URI(baseUrl + port + "/auth/login");

		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6ImpvaG4uZG9lQHNtdS5lZHUuc2cifQ.bTSJrbj-HRCavDhEF4AH9TK4pOh3uImqnkYwmEPxn78";

		GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
		GoogleIdToken.Payload payload = mock (GoogleIdToken.Payload.class);

		when(verifier.verify(token)).thenReturn(idTokenMock);
		when(idTokenMock.getPayload()).thenReturn(payload);
		when(payload.getSubject()).thenReturn(null);

		LoginDetails loginDetails1 = new LoginDetails(token);

		// Act
		ResponseEntity<String> result = restTemplate.postForEntity(uri, loginDetails1, String.class);

		// Assert
		assertEquals(403, result.getStatusCode().value());
		assertNotNull(result);
		verify(verifier).verify(token);
		verify(idTokenMock).getPayload();
	}

	@Test
	public void logout_Success() throws Exception {
		// Arrange
		URI uri = new URI(baseUrl + port + "/auth/logout");

		// Act
		ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

		// Assert
		assertEquals(200, result.getStatusCode().value());
	}
}
