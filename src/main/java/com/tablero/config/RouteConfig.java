package com.tablero.config;

import java.util.Map;

import com.tablero.controller.AuthController;
import com.tablero.controller.BoardController;
import com.tablero.controller.TaskController;
import com.tablero.controller.UserController;
import com.tablero.middleware.AuthMiddleware;
import com.tablero.middleware.RoleMiddleware;

import io.javalin.Javalin;

public class RouteConfig {
    private final AuthController authController;
    private final UserController userController;
    private final BoardController boardController;
    private final TaskController taskController;

    public RouteConfig(AuthController authController, UserController userController, BoardController boardController, TaskController taskController) {
        this.authController = authController;
        this.userController = userController;
        this.boardController = boardController;
        this.taskController = taskController;
    }

    public void registerRoutes(Javalin app) {
        app.get("/", ctx -> ctx.json(Map.of("App corriendo", "En el puerto 7000")));
        app.post("/api/auth/login", ctx -> authController.login(ctx)); // puede usarse method reference tambien --->>>
                                                                       // authController::login

        app.before("/api/protected/*", ctx -> AuthMiddleware.validateJWT(ctx));

        app.before("/api/protected/admin/*", ctx -> RoleMiddleware.requireAdmin(ctx));

        // admin routes
        app.get("/api/protected/admin/users", ctx -> userController.getAllUsers(ctx));

        app.get("/api/protected/admin/boards", ctx -> boardController.getAllBoards(ctx));

        app.post("/api/protected/admin/users", ctx -> userController.create(ctx));

        app.post("/api/protected/admin/boards", ctx -> boardController.createBoard(ctx));

        app.post("/api/protected/admin/boards/{boardId}/users", ctx -> boardController.assignUserToBoard(ctx));

        app.get("/api/protected/admin/boards/{boardId}/available-users", ctx -> boardController.getAvailableUsers(ctx));

        // user routes
        app.get("/api/protected/boards/my-boards", ctx -> boardController.getUserBoards(ctx));

        app.get("/api/protected/profile", ctx -> userController.getProfile(ctx));

        // user routes for tasks
       
        app.post("/api/protected/boards/{boardId}/tasks", ctx -> taskController.createTask(ctx));
        
        app.get("/api/protected/boards/{boardId}/tasks", ctx -> taskController.getAllTasksByBoard(ctx));
        
        app.put("/api/protected/boards/{boardId}/tasks/{taskId}", ctx -> taskController.updateTask(ctx));
        
        app.put("/api/protected/boards/{boardId}/tasks/{taskId}/state", ctx -> taskController.updateTaskState(ctx));
        
        app.delete("/api/protected/boards/{boardId}/tasks/{taskId}", ctx -> taskController.deleteTask(ctx));
    }
}
