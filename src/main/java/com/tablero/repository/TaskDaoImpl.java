package com.tablero.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tablero.config.DatabaseConfig;
import com.tablero.model.Task;
import com.tablero.model.TaskState;
import com.tablero.utils.StateMapper;

public class TaskDaoImpl implements TaskDao {
    private static final String INSERT_TASK = "INSERT INTO TASKS (description, state, board_id) VALUES (?, ?, ?)";
    private static final String SELECT_BY_BOARD_ID = "SELECT * FROM TASKS WHERE board_id = ?";
    private static final String SELECT_BY_ID = "SELECT id, description, state, board_id FROM TASKS WHERE id = ?";
    private static final String UPDATE_TASK = "UPDATE TASKS SET description = ?, state = ? WHERE id = ?";
    private static final String UPDATE_TASK_STATE = "UPDATE TASKS SET state = ? WHERE id = ?";
    private static final String DELETE_TASK = "DELETE FROM TASKS WHERE id = ?";

    @Override
    public Task create(Task task) {
        Task insertedTask = null;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getDescription());
            stmt.setString(2, StateMapper.stateToString(task.getState()));
            stmt.setInt(3, task.getBoardId());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    insertedTask = new Task(id, task.getDescription(), task.getState(), task.getBoardId());
                }
            }
        } catch (Exception e) {
            System.out.println("Error inserting task: " + e.getMessage());
        }

        return insertedTask;
    }

    @Override
    public Optional<Task> getById(int id) {
        Optional<Task> foundTask = Optional.empty();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task(
                            rs.getInt("id"),
                            rs.getString("description"),
                            StateMapper.stringToState(rs.getString("state")),
                            rs.getInt("board_id"));
                    foundTask = Optional.of(task);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting tasl by id: " + e);
        }

        return foundTask;
    }

    @Override
    public List<Task> getTasksByBoardId(int taskBoardId) {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_BOARD_ID)) {
            stmt.setInt(1, taskBoardId);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Task task = new Task(
                            rs.getInt("id"),
                            rs.getString("description"),
                            StateMapper.stringToState(rs.getString("state")),
                            rs.getInt("board_id"));

                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting tasks by board id: " + e.getMessage());
        }

        return tasks;
    }

    @Override
    public Task update(Task task) {

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_TASK)) {
            stmt.setString(1, task.getDescription());
            stmt.setString(2, StateMapper.stateToString(task.getState()));
            stmt.setInt(3, task.getId());

            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updatig task: " + e.getMessage());
            return null;
        }

        return task;
    }

    @Override
    public boolean updateState(int taskId, TaskState state) {
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_TASK_STATE)) {

                    stmt.setString(1, StateMapper.stateToString(state));
                    stmt.setInt(2, taskId);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) {
                        return false;
                    }
            
        } catch (Exception e) {
           System.out.println("Error updating task state: " + e.getMessage());
           return false;
        }

        return true;
    }

    @Override
    public boolean delete(int taskId) {
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_TASK)) {

                    stmt.setInt(1, taskId);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) {
                        return false;
                    }
            
        } catch (Exception e) {
           System.out.println("Error deleting task: " + e.getMessage());
           return false;
        }

        return true;
    }

}
