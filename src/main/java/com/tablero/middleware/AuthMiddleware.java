package com.tablero.middleware;

import com.tablero.utils.JwtUtil;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public class AuthMiddleware {
    public static void validateJWT(Context ctx){
        String header = ctx.header("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw  new UnauthorizedResponse("Unauthorized, absent token");
        }

        String token = header.substring(7);

        try {
            Claims claims = JwtUtil.validateToken(token);
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            throw new UnauthorizedResponse("Invalid token: " + e.getMessage());
        }

        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);
        String userId = JwtUtil.getUserIdFromToken(token);

        ctx.attribute("username", username);
        ctx.attribute("role", role);
        ctx.attribute("userId", userId);
    }
}
