package com.tablero.repository;

import java.util.List;
import java.util.Optional;

import com.tablero.model.User;

public interface UserDao {
    public User save (User user);
    public Optional<User> findByName(String username);
    public Optional<User> findById(int userId);
    public List<User> getAllUsers();
    public boolean isUserAssignedToBoard(int userId, int boardId);
}
