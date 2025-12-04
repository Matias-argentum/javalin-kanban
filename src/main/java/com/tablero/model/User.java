package com.tablero.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private Role role;
    
    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(int id, String passwordHash, Role role, String username) {
        this.id = id;
        this.passwordHash = passwordHash;
        this.role = role;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        sb.append("id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", passwordHash=").append(passwordHash);
        sb.append(", role=").append(role);
        sb.append('}');
        return sb.toString();
    }
}
