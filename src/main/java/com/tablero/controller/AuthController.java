package com.tablero.controller;

import com.tablero.dtos.AuthResponse;
import com.tablero.dtos.LoginRequest;
import com.tablero.service.AuthService;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void login(Context ctx) {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        AuthResponse response = null;
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadRequestResponse("Username or Password cannot be null");
        }

        try {
            response = authService.login(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new UnauthorizedResponse("Error during login");
        }

        ctx.status(200).json(response);
    }
}
