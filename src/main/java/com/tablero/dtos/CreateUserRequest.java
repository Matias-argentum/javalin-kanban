package com.tablero.dtos;

import com.tablero.model.Role;

public class CreateUserRequest {
    private String username;
    private String password;
    private Role role;

    public CreateUserRequest(String password, Role role, String username) {
        this.password = password;
        this.role = role;
        this.username = username;
    }

    public CreateUserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
