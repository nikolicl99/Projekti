package com.asss.www.ApotekarskaUstanova.Dto;

public class JwtResponse {

    private static String token;

    // Konstruktor
    public JwtResponse(String token) {
        this.token = token;
    }

    // Getter i setter
    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        JwtResponse.token = token;
    }
}
