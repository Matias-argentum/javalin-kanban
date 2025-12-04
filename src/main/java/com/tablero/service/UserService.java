package com.tablero.service;

import java.util.List;
import java.util.Optional;

import com.tablero.model.Role;
import com.tablero.model.User;
import com.tablero.repository.UserDao;
import com.tablero.utils.PasswordUtil;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    public User createUser(String username, String password, Role role){
        
        Optional<User> foundUser = userDao.findByName(username);
        if (!foundUser.isEmpty()) {
            throw new RuntimeException("Username already exists on db");
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }
        
        User newUser = new User(username, PasswordUtil.hashPassword(password), role);
        return userDao.save(newUser);    
    }

    public List<User> getAllUsers(){
        return userDao.getAllUsers();
    }
}
