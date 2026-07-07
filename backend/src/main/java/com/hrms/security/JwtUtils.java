package com.hrms.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

// ==============================================================================
// JWT UTILITIES CLASS
// ==============================================================================
// Responsible for token generation and verification using Auth0 Java-JWT library.
// ==============================================================================

@Component
public class JwtUtils {

    @Value("${app.jwt.secret:dGhpcy1pcy1hLXNlY3JldC1rZXktZm9yLWhybXMtYXBwbGljYXRpb24td2hpY2gtaXMtc3VmZmljaWVudGx5LWxvbmc=}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}") // Default 24 hours
    private long jwtExpirationMs;

    // Generate token containing username, email, and role claims
    public String generateToken(String username, String email, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("email", email)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    // Validate token and extract the username (subject)
    public String validateTokenAndGetUsername(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    // Validate token and extract the role claim
    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("role").asString();
    }
}
