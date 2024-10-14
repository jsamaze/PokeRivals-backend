package com.smu.csd.pokerivals.controller;

import com.smu.csd.pokerivals.persistence.entity.user.Player;
import com.smu.csd.pokerivals.record.Message;
import com.smu.csd.pokerivals.security.authentication.IncompleteGoogleAuthentication;
import com.smu.csd.pokerivals.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService service){
        this.playerService = service;
    }

    @Getter
    public static class PlayerRegistrationDTO {

        private Player player;

        @NotEmpty(message = "Google ID token must be provided")
        @NotNull(message = "Google ID cannot be null")
        private String credentials;

        public IncompleteGoogleAuthentication getAuthentication() throws UnsupportedEncodingException{
            return new IncompleteGoogleAuthentication(credentials);
        }
    }

    @PostMapping("")
    @Operation(summary= "Register player",
            description = "Registers a player for an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player registered successfully.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "400", description = "Player failed to be registered.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class)) })})
    public Message register(@RequestBody @Valid PlayerRegistrationDTO dto) throws UnsupportedEncodingException{
        playerService.register(dto.getPlayer(), dto.getAuthentication());
        return new Message("Player registered successfully");
    }


}
