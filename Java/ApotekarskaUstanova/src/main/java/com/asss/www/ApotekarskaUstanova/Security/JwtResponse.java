package com.asss.www.ApotekarskaUstanova.Security;

public class JwtResponse {

    private static String token;
    private static int userId; // Čuva ID zaposlenog
    private static int typeId;

    public JwtResponse(String token, int userId, int typeId) {
        this.token = token;
        this.userId = userId;
        this.typeId = typeId;
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

    public static int getTypeId() {
        return typeId;
    }

    public static void setTypeId(int typeId) {
        JwtResponse.typeId = typeId;
    }
}
