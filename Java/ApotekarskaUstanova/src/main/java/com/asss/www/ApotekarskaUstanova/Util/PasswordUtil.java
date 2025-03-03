package com.asss.www.ApotekarskaUstanova.Util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // Metoda za heširanje lozinke
    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(10); // Cost factor 10
        return BCrypt.hashpw(plainPassword, salt);
    }

    // Metoda za proveru lozinke
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

