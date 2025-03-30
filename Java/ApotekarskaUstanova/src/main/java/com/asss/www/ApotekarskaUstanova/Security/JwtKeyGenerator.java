package com.asss.www.ApotekarskaUstanova.Security;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import javax.crypto.KeyGenerator;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            Key key = keyGen.generateKey();
            String secretKey = java.util.Base64.getEncoder().encodeToString(key.getEncoded());

            saveToEnvFile(secretKey);

            System.out.println("Generated and saved key to .env file");
        } catch (Exception e) {
            System.err.println("Error generating key: " + e.getMessage());
        }
    }

    private static void saveToEnvFile(String secretKey) {
        String envFilePath = ".env";
        try {
            boolean fileExists = Files.exists(Paths.get(envFilePath));

            try (FileWriter writer = new FileWriter(envFilePath, true)) {
                if (!fileExists) {
                    writer.write("# JWT Configuration\n");
                }
                writer.write("JWT_SECRET=" + secretKey + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving key to .env file: " + e.getMessage());
        }
    }
}