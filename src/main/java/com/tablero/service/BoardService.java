package com.tablero.service;

import java.util.List;
import java.util.Optional;

import com.tablero.model.TaskBoard;
import com.tablero.model.User;
import com.tablero.repository.TaskBoardDao;
import com.tablero.repository.UserDao;

public class BoardService {
    private TaskBoardDao taskBoardDao;
    private UserDao userDao;

    public BoardService(TaskBoardDao taskBoardDao, UserDao userDao) {
        this.taskBoardDao = taskBoardDao;
        this.userDao = userDao;
    }

    public TaskBoard createBoard(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty");
        }
        return taskBoardDao.create(new TaskBoard(name));
    }

    public void assignUserToBoard(int boardId, int userId) {
        Optional<User> foundUser = userDao.findById(userId);
        if (foundUser.isEmpty()) {
            throw new RuntimeException("User Does Not Exist");
        }

        Optional<TaskBoard> foundBoard = taskBoardDao.getById(boardId);
        if (foundBoard.isEmpty()) {
            throw new RuntimeException("TaskBoard Does Not Exist");
        }

        List<User> usersInBoard = taskBoardDao.getAllUsersByBoard(boardId);

        boolean alreadyAssigned = usersInBoard.stream()
                .anyMatch(user -> user.getId() == userId);
        if (alreadyAssigned) {
            throw new RuntimeException("User is already assigned to the board");
        }

        taskBoardDao.assignUserToBoard(userId, boardId);
    }

    public List<TaskBoard> getUserBoards(int userId) {
        Optional<User> foundUser = userDao.findById(userId);
        if (foundUser.isEmpty()) {
            throw new RuntimeException("User Does Not Exist");
        }

        return taskBoardDao.getUserBoards(userId);
    }

    public List<User> getAllUsersNotInBoard(int boardId) {
        Optional<TaskBoard> foundBoard = taskBoardDao.getById(boardId);
        if (foundBoard.isEmpty()) {
            throw new RuntimeException("TaskBoard Does Not Exist");
        }

        return taskBoardDao.getAllUsersNotInBoard(boardId);
    }

    public List<TaskBoard> getAllBoards(){
        return taskBoardDao.listTaskBoards();
    }
}
