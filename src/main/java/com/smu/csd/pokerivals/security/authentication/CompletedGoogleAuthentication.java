package com.smu.csd.pokerivals.security.authentication;

import com.smu.csd.pokerivals.persistence.entity.user.Admin;
import com.smu.csd.pokerivals.persistence.entity.user.Player;
import com.smu.csd.pokerivals.persistence.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CompletedGoogleAuthentication implements Authentication {

    private final MyUserDetails myUserDetails;
    private final String googleSub;
    private boolean authenticated= true;
    private final String name; // username!

    public class MyUserDetails implements UserDetails {

        @Getter
        private final String username;
        private final Collection<GrantedAuthority> authoritiesList = new ArrayList<>();

        public MyUserDetails(User user){
            this.username = user.getUsername();
            if (user instanceof Player){
                authoritiesList.add(
                        new SimpleGrantedAuthority("PLAYER")
                );
            } else if (user instanceof Admin){
                authoritiesList.add(
                        new SimpleGrantedAuthority("ADMIN")
                );
            }
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authoritiesList;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }


    public CompletedGoogleAuthentication(User user){
        this.myUserDetails  = new MyUserDetails(user);
        this.googleSub = user.getGoogleSub();
        this.name = user.getUsername();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return myUserDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return googleSub;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return myUserDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
        if (arg0){
            throw new IllegalArgumentException("Create a new authentication!");
        } else {
            this.authenticated = false;
        }
    }

}
