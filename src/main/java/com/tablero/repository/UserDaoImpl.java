package com.tablero.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tablero.config.DatabaseConfig;
import com.tablero.model.User;
import com.tablero.utils.RoleMapper;

public class UserDaoImpl implements UserDao {

    private static final String SELECT_BY_USERNAME = "SELECT * FROM USERS WHERE username = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM USERS WHERE id = ?";
    private static final String INSERT_USER = "INSERT INTO USERS (username, password_hash, role) VALUES (?,?,?)";
    private static final String SELECT_ALL_USERS = "SELECT * FROM USERS";
    private static final String SELECT_COUNT_USER_ASSIGNED = "SELECT COUNT(*) > 0 FROM USERS_TASKBOARDS WHERE user_id = ? AND board_id = ?";

    @Override
    public User save(User user) {

        User insertedUser = null;

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().toString());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {
                    int id = rs.getInt(1);

                    insertedUser = new User(id, user.getPasswordHash(), user.getRole(), user.getUsername());
                }
            }

        } catch (Exception e) {
            System.out.println("Error when saving user: ");
            System.out.println(e.getMessage());
        }
        return insertedUser;
    }

    @Override
    public Optional<User> findByName(String username) {
        Optional<User> foundUser = Optional.empty();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("password_hash"),
                            RoleMapper.stringToRole(rs.getString("role")),
                            rs.getString("username"));

                    foundUser = Optional.of(user);

                }
            }
        } catch (Exception e) {
            System.out.println("Error when retrieving user: ");
            System.out.println(e.getMessage());
        }

        return foundUser;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("password_hash"),
                            RoleMapper.stringToRole(rs.getString("role")),
                            rs.getString("username"));

                    users.add(user);
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public Optional<User> findById(int userId) {
        Optional<User> foundUser = Optional.empty();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("password_hash"),
                            RoleMapper.stringToRole(rs.getString("role")),
                            rs.getString("username"));

                    foundUser = Optional.of(user);

                }
            }
        } catch (Exception e) {
            System.out.println("Error when retrieving user: ");
            System.out.println(e.getMessage());
        }

        return foundUser;
    }

    @Override
    public boolean isUserAssignedToBoard(int userId, int boardId) {
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COUNT_USER_ASSIGNED)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, boardId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error when retrieving assignation: ");
            System.out.println(e.getMessage());
        }
        return false;
    }

}
