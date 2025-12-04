package com.tablero.repository;

import java.util.List;
import java.util.Optional;

import com.tablero.model.TaskBoard;
import com.tablero.model.User;

public interface TaskBoardDao {
    public TaskBoard create(TaskBoard taskBoard);

    public Optional<TaskBoard> getById(int id);

    public List<TaskBoard> listTaskBoards();

    public boolean assignUserToBoard(int userId, int boardId);

    public List<User> getAllUsersByBoard(int boardId);

    public List<User> getAllUsersNotInBoard(int boardId);

    public List<TaskBoard> getUserBoards(int userId);
}
