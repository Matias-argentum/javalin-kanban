package com.tablero.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tablero.config.DatabaseConfig;
import com.tablero.model.TaskBoard;
import com.tablero.model.User;
import com.tablero.utils.RoleMapper;

public class TaskBoardDaoImpl implements TaskBoardDao {

    private static final String SELECT_BY_ID = "SELECT id, name FROM TASKBOARDS WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM TASKBOARDS";
    private static final String INSERT_BOARD = "INSERT INTO TASKBOARDS (name) VALUES (?)";
    private static final String INSERT_USERS_TASKBOARDS = "INSERT INTO USERS_TASKBOARDS (user_id, board_id) VALUES (?, ?)";
    private static final String SELECT_USERS_BY_TASKBOARD = "SELECT U.* FROM USERS U JOIN USERS_TASKBOARDS UT ON U.id = UT.user_id WHERE UT.board_id = ?";
    private static final String SELECT_USERS_NOT_IN_TASKBOARD = "SELECT * FROM USERS WHERE id NOT IN(SELECT user_id FROM USERS_TASKBOARDS WHERE board_id = ?)";
    private static final String SELECT_TASKBOARDS_OF_PARTICULAR_USER = "SELECT T.* FROM TASKBOARDS T JOIN USERS_TASKBOARDS UT ON T.id = UT.board_id WHERE UT.user_id = ?";
    

    @Override
    public TaskBoard create(TaskBoard taskBoard) {
        TaskBoard insertedTaskBoard = null;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOARD, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, taskBoard.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    insertedTaskBoard = new TaskBoard(id, taskBoard.getName());
                }
            }

        } catch (Exception e) {
            System.out.println("Error inserting taskboard: " + e.getMessage());
        }

        return insertedTaskBoard;
    }

    @Override
    public Optional<TaskBoard> getById(int id) {
        Optional<TaskBoard> foundTaskBoard = Optional.empty();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TaskBoard board = new TaskBoard(rs.getInt("id"), rs.getString("name"));
                    foundTaskBoard = Optional.of(board);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting taskBoard by id: " + e.getMessage());
        }

        return foundTaskBoard;
    }

    @Override
    public List<TaskBoard> listTaskBoards() {
        List<TaskBoard> boards = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskBoard board = new TaskBoard(rs.getInt("id"), rs.getString("name"));
                    boards.add(board);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting taskBoards: " + e.getMessage());
        }

        return boards;
    }

    @Override
    public boolean assignUserToBoard(int userId, int boardId) {
        boolean result = false;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USERS_TASKBOARDS, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, boardId);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    result = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error assigning user to board");
            System.out.println(e.getMessage());
        }

        return result;
    }

    @Override
    public List<User> getAllUsersByBoard(int boardId) {
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USERS_BY_TASKBOARD)) {

            stmt.setInt(1, boardId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("password_hash"),
                            RoleMapper.stringToRole(rs.getString("role")),
                            rs.getString("username")
                    );
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting all users by taskboard");
            System.out.println(e.getMessage());
        }

        return users;
    }

    @Override
    public List<User> getAllUsersNotInBoard(int boardId) {
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USERS_NOT_IN_TASKBOARD)) {

            stmt.setInt(1, boardId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("password_hash"),
                            RoleMapper.stringToRole(rs.getString("role")),
                            rs.getString("username")
                    );
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting all users not in taskboard");
            System.out.println(e.getMessage());
        }

        return users;
    }

    @Override
    public List<TaskBoard> getUserBoards(int userId) {
        List<TaskBoard> boards = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TASKBOARDS_OF_PARTICULAR_USER)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskBoard board = new TaskBoard(rs.getInt("id"), rs.getString("name"));
                    boards.add(board);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting taskBoards of user: " + e.getMessage());
        }

        return boards;
    }
}
