package com.smu.csd.pokerivals.service;

import com.smu.csd.pokerivals.persistence.entity.user.*;
import com.smu.csd.pokerivals.persistence.repository.ClanRepository;
import com.smu.csd.pokerivals.persistence.repository.PlayerPagingRepository;
import com.smu.csd.pokerivals.persistence.repository.PlayerRepository;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GoogleIdTokenVerifier verifier;
    private final PlayerPagingRepository playerPagingRepository;
    private final ClanRepository clanRepository;

    @Autowired
    public PlayerService(PlayerRepository repo, GoogleIdTokenVerifier verifier, PlayerPagingRepository playerPagingRepository, ClanRepository clanRepository){
        this.playerRepository= repo;
        this.verifier = verifier;
        this.playerPagingRepository = playerPagingRepository;
        this.clanRepository = clanRepository;
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

    public List<Player> getFriendsOf(String username){
        return playerRepository.findFriendsOfPlayer(username);
    }

    @Transactional
    @RolesAllowed("ROLE_PLAYER")
    public void connectAsFriends(String username1, String username2){
        var player1 = playerRepository.findById(username1).orElseThrow();
        var player2 = playerRepository.findById(username2).orElseThrow();
        player1.addFriend(player2);
        playerRepository.save(player1);
        playerRepository.save(player2);
    }

    @Transactional
    @RolesAllowed("ROLE_PLAYER")
    public void disconnectAsFriends(String username1, String username2){
        var player1 = playerRepository.findById(username1).orElseThrow();
        var player2 = playerRepository.findById(username2).orElseThrow();
        player1.removeFriend(player2);
        playerRepository.save(player1);
        playerRepository.save(player2);
    }

    public List<Player> searchPlayers(String query){
        return playerRepository.findByUsernameContaining(query);
    }


    public Player getUser(String username){
        return playerRepository.findById(username).orElseThrow();
    }

    @Transactional
    public void addToClan(String username, String clanName){
        Player player = playerRepository.findById(username).orElseThrow();
        Clan clan = clanRepository.findById(clanName).orElseThrow();

        player.addToClan(clan);
        playerRepository.save(player);
        clanRepository.save(clan);
    }

}
