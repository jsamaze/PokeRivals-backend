package com.smu.csd.pokerivals.unit;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.smu.csd.pokerivals.user.repository.PlayerRepository;
import com.smu.csd.pokerivals.user.service.PlayerService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Autowired
    @InjectMocks
    private PlayerService playerService; 

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GoogleIdTokenVerifier verifier;

    
    @Test
    public void register_validUser_noError(){

    }
}
