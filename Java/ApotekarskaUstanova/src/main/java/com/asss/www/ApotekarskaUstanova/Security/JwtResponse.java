package com.asss.www.ApotekarskaUstanova.Security;

public class JwtResponse {

    private static String token;
    private static int userId; // Čuva ID zaposlenog

    public JwtResponse(String token, int userId) {
        this.token = token;
        this.userId = userId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        JwtResponse.token = token;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        JwtResponse.userId = userId;
    }
}
