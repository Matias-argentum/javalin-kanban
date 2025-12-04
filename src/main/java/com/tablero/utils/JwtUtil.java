package com.tablero.utils;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    private static final String SECRET_KEY = "Era más blanda que el agua, que el agua blanda";
    private static final int EXPIRATION = 900000;

    public static String generateToken(String username, String role, String userId) {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public static Claims validateToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    public static String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    public static String getUserIdFromToken(String token){
        Claims claims = validateToken(token);
        return claims.get("userId", String.class);
    }

    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
}
