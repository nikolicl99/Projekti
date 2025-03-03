package com.asss.www.ApotekarskaUstanova.Security;

import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import javax.crypto.KeyGenerator;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        try {
            // Generisanje tajnog ključa
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256); // Veličina ključa u bitovima
            Key key = keyGen.generateKey();
            String secretKey = java.util.Base64.getEncoder().encodeToString(key.getEncoded());

            // Sačuvajte ključ u application.properties fajl
            saveToPropertiesFile(secretKey);

            System.out.println("Generated and saved key: " + secretKey);
        } catch (Exception e) {
            System.err.println("Error generating key: " + e.getMessage());
        }
    }

    private static void saveToPropertiesFile(String secretKey) {
        String propertiesFilePath = "src/main/resources/application.properties";
        try (FileWriter writer = new FileWriter(propertiesFilePath, true)) {
            writer.write("\njwt.secret=" + secretKey + "\n");
            System.out.println("Key saved to application.properties.");
        } catch (IOException e) {
            System.err.println("Error saving key to application.properties: " + e.getMessage());
        }
    }
}

