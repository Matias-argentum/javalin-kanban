package com.tablero.service;

import java.util.List;
import java.util.Optional;

import com.tablero.model.Task;
import com.tablero.model.TaskState;
import com.tablero.repository.TaskDao;
import com.tablero.repository.UserDao;

public class TaskService {
    private final TaskDao taskDao;
    private final UserDao userDao;

    public TaskService(TaskDao taskDao, UserDao userDao) {
        this.taskDao = taskDao;
        this.userDao = userDao;
    }

    public Task create(int userId, String description, TaskState state, int boardId) {
        if (!userDao.isUserAssignedToBoard(userId, boardId)) {
            throw new RuntimeException("User not authorized for this board");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Description cannot be empty");
        }

        Task task = new Task(description, state, boardId);
        Task createdTask = taskDao.create(task);

        if (createdTask == null) {
            throw new RuntimeException("Failed to create task in DB");
        }

        return createdTask;
    }

    public Task findById(int taskId) {
        Optional<Task> foundTask = taskDao.getById(taskId);
        if (foundTask.isEmpty()) {
            throw new RuntimeException("Task not found in DB");
        }
        return foundTask.get();
    }

    public List<Task> findAllByBoard(int boardId) {
        return taskDao.getTasksByBoardId(boardId);
    }

    public Task update(int id, String description, TaskState state, int boardId, int userId) {
        if (!userDao.isUserAssignedToBoard(userId, boardId)) {
            throw new RuntimeException("User not authorized for this board");
        }
        Optional<Task> foundTask = taskDao.getById(id);

        if (foundTask.isEmpty()) {
            throw new RuntimeException("Task not found in DB");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Description cannot be empty");
        }

        Task updatedTask = foundTask.get();

        updatedTask.setDescription(description);
        updatedTask.setState(state);

        return taskDao.update(updatedTask);
    }

    public Task updateTaskState(int taskId, TaskState state, int boardId, int userId) {
        if (!userDao.isUserAssignedToBoard(userId, boardId)) {
            throw new RuntimeException("User not authorized for this board");
        }
        Optional<Task> foundTask = taskDao.getById(taskId);

        if (foundTask.isEmpty()) {
            throw new RuntimeException("Task not found in DB");
        }
        boolean result = taskDao.updateState(taskId, state);

        if (!result) {
            throw new RuntimeException("Failed to update task state in DB");
        }
        Task taskToReturn = foundTask.get();

        taskToReturn.setState(state);
        return taskToReturn;

    }

    public boolean delete(int taskId, int boardId, int userId) {
        if (!userDao.isUserAssignedToBoard(userId, boardId)) {
            throw new RuntimeException("User not authorized for this board");
        }
        Optional<Task> foundTask = taskDao.getById(taskId);

        if (foundTask.isEmpty()) {
            throw new RuntimeException("Task not found in DB");
        }

        return taskDao.delete(taskId);
    }
}
