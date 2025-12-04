package com.tablero.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tablero.dtos.AssignUserRequest;
import com.tablero.dtos.AssignUserResponse;
import com.tablero.dtos.BoardResponse;
import com.tablero.dtos.CreateBoardRequest;
import com.tablero.dtos.UserResponse;
import com.tablero.model.TaskBoard;
import com.tablero.service.BoardService;
import com.tablero.utils.UserMapper;

import io.javalin.http.Context;

public class BoardController {
    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    public void createBoard(Context ctx) {
        CreateBoardRequest request = ctx.bodyAsClass(CreateBoardRequest.class);
        TaskBoard createdTaskBoard = boardService.createBoard(request.getName());

        BoardResponse response = new BoardResponse();
        response.setId(createdTaskBoard.getId());
        response.setName(createdTaskBoard.getName());

        ctx.status(201).json(response);
    }

    public void assignUserToBoard(Context ctx) {
        AssignUserRequest request = ctx.bodyAsClass(AssignUserRequest.class);
        AssignUserResponse response = new AssignUserResponse();
        int boardId = Integer.parseInt(ctx.pathParam("boardId"));

        try {
            boardService.assignUserToBoard(boardId, request.getUserId());
            response.setMessage("Board assigned to user succesfully!");
            ctx.status(200).json(response);
        } catch (RuntimeException e) {
            response.setMessage("An error has ocurred while assingning board!");
            ctx.status(409).json(response);
        }

    }

    public void getAllBoards(Context ctx) {

        List<BoardResponse> boards = boardService.getAllBoards()
                .stream()
                .map(tb -> new BoardResponse(tb.getId(), tb.getName()))
                .collect(Collectors.toList());

        ctx.status(200).json(Map.of("all boards", boards));
    }

    public void getUserBoards(Context ctx) {
        int userId = Integer.parseInt(ctx.attribute("userId"));
        try {
            List<BoardResponse> boardsResponse = boardService.getUserBoards(userId)
                    .stream()
                    .map(tb -> new BoardResponse(tb.getId(), tb.getName()))
                    .collect(Collectors.toList());

            ctx.status(200).json(boardsResponse);

        } catch (RuntimeException e) {
            ctx.status(409).json(Map.of("error", e.getMessage()));
        }

    }

    public void getAvailableUsers(Context ctx) {
        int boardId = Integer.parseInt(ctx.pathParam("boardId"));
        try {
            List<UserResponse> usersResponse = boardService.getAllUsersNotInBoard(boardId)
                    .stream()
                    .map(user -> UserMapper.userToResponse(user))
                    .collect(Collectors.toList());

            ctx.status(200).json(usersResponse);
        } catch (Exception e) {
            ctx.status(409).json(Map.of("error", e.getMessage()));
        }
    }
}
