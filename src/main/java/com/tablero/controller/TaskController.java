package com.tablero.controller;

import java.util.List;
import java.util.Map;

import com.tablero.dtos.CreateTaskRequest;
import com.tablero.dtos.UpdateTaskRequest;
import com.tablero.dtos.UpdateTaskStateRequest;
import com.tablero.model.Task;
import com.tablero.model.TaskState;
import com.tablero.service.TaskService;
import com.tablero.utils.StateMapper;
import com.tablero.websockets.WebsocketManager;

import io.javalin.http.Context;

public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public void createTask(Context ctx) {
        CreateTaskRequest request = ctx.bodyAsClass(CreateTaskRequest.class);

        int boardId;
        int userId;

        try {
            boardId = Integer.parseInt(ctx.pathParam("boardId"));
            userId = Integer.parseInt(ctx.attribute("userId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Bad Request"));
            return;
        }

        String description = request.getDescription();
        TaskState state = null;
        try {
            state = StateMapper.stringToState(request.getState());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            ctx.status(400).json(Map.of("error", "Invalid state"));
            return;
        }

        try {
            Task newTask = taskService.create( userId, description, state, boardId);
            WebsocketManager.broadcast(String.valueOf(boardId), newTask);
            ctx.status(201).json(Map.of("task", newTask));
            return;
        } catch (Exception e) {
            if (e.getMessage().contains("cannot be empty")) {
                ctx.status(400).json(Map.of("error", "Bad Request"));
                return;
            } else {
                ctx.status(500).json(Map.of("error", "Server error"));
                return;
            }
        }

    }

    public void updateTask(Context ctx) {
        UpdateTaskRequest request = ctx.bodyAsClass(UpdateTaskRequest.class);
        int id;
        int userId;
        int boardId;
        try {
            id = Integer.parseInt(ctx.pathParam("taskId"));
            userId = Integer.parseInt(ctx.attribute("userId"));
            boardId = Integer.parseInt(ctx.pathParam("boardId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Bad Request"));
            return;
        }

        String description = request.getDescription();
        TaskState state = null;
        try {
            state = StateMapper.stringToState(request.getState());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            ctx.status(400).json(Map.of("error", "Invalid state"));
            return;
        }

        try {
            Task updatedTask = taskService.update(id, description, state, boardId, userId);
            WebsocketManager.broadcast(String.valueOf(boardId), updatedTask);
            ctx.status(200).json(Map.of("task", updatedTask));
            return;
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                ctx.status(404).json(Map.of("error", "Not Found"));
                return;
            } else if (e.getMessage().contains("cannot be empty")) {
                ctx.status(400).json(Map.of("error", "Bad Request"));
                return;
            } else {
                ctx.status(500).json(Map.of("error", "Server error"));
                return;
            }
        }
    }

    public void updateTaskState(Context ctx) {
        UpdateTaskStateRequest request = ctx.bodyAsClass(UpdateTaskStateRequest.class);

        int id;
        int userId;
        int boardId;
        try {
            id = Integer.parseInt(ctx.pathParam("taskId"));
            userId = Integer.parseInt(ctx.attribute("userId"));
            boardId = Integer.parseInt(ctx.pathParam("boardId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Bad Request"));
            return;
        }

        TaskState state = null;
        try {
            state = StateMapper.stringToState(request.getState());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            ctx.status(400).json(Map.of("error", "Invalid state"));
            return;
        }

        try {

            Task updatedTask = taskService.updateTaskState(id, state, boardId, userId);

            WebsocketManager.broadcast(String.valueOf(boardId), updatedTask);

            ctx.status(200).json(Map.of("task", updatedTask));
            return;
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                ctx.status(404).json(Map.of("error", "Not Found"));
                return;
            } else {
                ctx.status(500).json(Map.of("error", "Server error"));
                return;
            }
        }
    }

    public void getAllTasksByBoard(Context ctx) {
        int boardId;

        try {
            boardId = Integer.parseInt(ctx.pathParam("boardId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Bad Request"));
            return;
        }
        try {
            List<Task> tasks = taskService.findAllByBoard(boardId);
            ctx.status(200).json(tasks);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    public void deleteTask(Context ctx) {
        int taskId;
        int boardId;
        int userId;
        try {
            taskId = Integer.parseInt(ctx.pathParam("taskId"));
            boardId = Integer.parseInt(ctx.pathParam("boardId"));
            userId = Integer.parseInt(ctx.attribute("userId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Bad Request"));
            return;
        }
        try {
            boolean result = taskService.delete(taskId, boardId, userId);

            if (result) {
                Task deletedTask = new Task();
                deletedTask.setId(taskId);
                //si la task no tiene descripcion ni estado es porque hay que borrarla
                WebsocketManager.broadcast(String.valueOf(boardId), deletedTask);
                ctx.status(200).json(Map.of("message", "Successfully deleted"));
            }

            return;
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                ctx.status(404).json(Map.of("error", "Not Found"));
                return;
            } else {
                ctx.status(500).json(Map.of("error", "Server error"));
                return;
            }
        }
    }
}
