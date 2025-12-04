package com.tablero.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tablero.dtos.CreateUserRequest;
import com.tablero.dtos.UserResponse;
import com.tablero.model.User;
import com.tablero.service.UserService;
import com.tablero.utils.UserMapper;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;

public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    
    public void getProfile(Context ctx){
        String username = ctx.attribute("username");
        String role = ctx.attribute("role");

        Map<String, String> userMap = new HashMap<>();

        userMap.put("username", username);
        userMap.put("role", role);
        
        ctx.status(200).json(userMap);
    }

    public void getAllUsers(Context ctx){
        
        List<UserResponse> users = userService.getAllUsers().stream().map( user ->  UserMapper.userToResponse(user)).collect(Collectors.toList());

        ctx.status(200).json(Map.of("all users", users));
    }

    public void create(Context ctx){
        CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);
        User createdUser = null;

        try {
            createdUser = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());
        } catch (Exception e) {
            System.out.println("Error creating user in controller: " + e.getMessage());
            throw new ConflictResponse();
        }
        UserResponse response = UserMapper.userToResponse(createdUser);

        ctx.status(201).json(response);
    }
}
