package com.tablero.utils;


import com.tablero.dtos.UserResponse;
import com.tablero.model.User;

public class UserMapper {
    public static UserResponse userToResponse(User user){
        return new UserResponse(user.getId(), user.getUsername(), RoleMapper.roleToString(user.getRole()));
    }
}
