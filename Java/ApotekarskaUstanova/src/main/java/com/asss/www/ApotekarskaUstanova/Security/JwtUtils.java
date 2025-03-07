package com.asss.www.ApotekarskaUstanova.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails, int userId) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userId) // Dodaj ID korisnika
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dan
                .signWith(getSigningKey())
                .compact();
    }


    public String extractUsername(String token) {
        return getClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return getClaims(token).getPayload().getExpiration();
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Novi način verifikacije
                .build()
                .parseSignedClaims(token);
    }
}
