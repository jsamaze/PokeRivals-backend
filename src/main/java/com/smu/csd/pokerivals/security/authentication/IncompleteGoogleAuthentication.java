package com.smu.csd.pokerivals.security.authentication;

import com.smu.csd.pokerivals.security.DecodedToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;

public class IncompleteGoogleAuthentication implements Authentication {

    private final String token;
    private final DecodedToken decodedToken;

    public IncompleteGoogleAuthentication(String token) throws UnsupportedEncodingException {
        this.token = token;
        this.decodedToken = DecodedToken.getDecoded(token);
    }

    @Override
    public String getName() {
        return decodedToken.getName(); // user's full name
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(null);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return decodedToken.getName();
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
        if (arg0){
            throw new IllegalArgumentException("Token is never authenticated!");
        }
    }

}

