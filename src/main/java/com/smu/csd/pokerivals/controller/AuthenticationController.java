package com.smu.csd.pokerivals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import com.smu.csd.pokerivals.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/auth")
public class AuthenticationController {

    @Getter
    private static class LoginDetails {
        private String credentials;
    }

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    @SneakyThrows
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDetails loginDetails, HttpServletRequest request, HttpServletResponse response) {
        log.trace("Received " + loginDetails.getCredentials());
        IncompleteGoogleAuthentication token = new IncompleteGoogleAuthentication(loginDetails.getCredentials());

        Authentication authentication = authenticationService.login(token, request, response);

        Map<String, String> myMap = new HashMap<>();
        myMap.put("username", authentication.getName());
        myMap.put("role", authentication.getAuthorities().toArray(new GrantedAuthority[1])[0].getAuthority());

        String json = new ObjectMapper().writeValueAsString(myMap);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> customLogout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return new ResponseEntity<>("User signed-out successfully.", HttpStatus.OK);
    }

}
