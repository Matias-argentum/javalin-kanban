package com.tablero.service;

import java.util.Optional;

import com.tablero.dtos.AuthResponse;
import com.tablero.dtos.LoginRequest;
import com.tablero.model.User;
import com.tablero.repository.UserDao;
import com.tablero.utils.JwtUtil;
import com.tablero.utils.PasswordUtil;
import com.tablero.utils.RoleMapper;

public class AuthService {
    private UserDao userDao;

    public AuthService(UserDao userDao){
        this.userDao = userDao;
    }

    public AuthResponse login(LoginRequest request){
        Optional<User> user = userDao.findByName(request.getUsername());

        if(user.isEmpty()){
            throw new IllegalArgumentException("El usuario noe xiste en al db.");
        }

        boolean isPasswordOk = PasswordUtil.checkPassword(request.getPassword(), user.get().getPasswordHash());

        if (!isPasswordOk) {
            throw new RuntimeException("Bad credentials");
        }

        String token = JwtUtil.generateToken(user.get().getUsername(), RoleMapper.roleToString(user.get().getRole()), Integer.toString(user.get().getId()));

        return new AuthResponse(token, user.get().getUsername(), RoleMapper.roleToString(user.get().getRole()));
    }
}
