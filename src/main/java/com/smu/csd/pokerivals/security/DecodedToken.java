package com.smu.csd.pokerivals.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecodedToken {

    private String sub;
    private String name;
    private String email;

    @SneakyThrows
    public static DecodedToken getDecoded(String encodedToken) {
        String[] pieces = encodedToken.split("\\.");
        String b64payload = pieces[1];
        String jsonString = new String(Base64.getDecoder().decode(b64payload), StandardCharsets.UTF_8);

        return new ObjectMapper().readValue(jsonString, DecodedToken.class);
    }

    @SneakyThrows
    public String toString() {
        return  new ObjectMapper().writeValueAsString(this);
    }

}