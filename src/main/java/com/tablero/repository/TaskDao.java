package com.tablero.repository;

import java.util.List;
import java.util.Optional;

import com.tablero.model.Task;
import com.tablero.model.TaskState;

public interface TaskDao {
    public Task create(Task task);
    public Task update(Task task);
    public boolean updateState(int taskId, TaskState state);
    public boolean  delete(int taskId);
    public Optional<Task> getById(int id);
    public List<Task> getTasksByBoardId(int taskBoardId);
}
