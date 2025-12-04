package com.tablero;

import java.sql.SQLException;

import org.h2.tools.Server;

import com.tablero.config.DatabaseConfig;
import com.tablero.config.RouteConfig;
import com.tablero.controller.AuthController;
import com.tablero.controller.BoardController;
import com.tablero.controller.TaskController;
import com.tablero.controller.UserController;
import com.tablero.model.Role;
import com.tablero.model.User;
import com.tablero.repository.TaskBoardDao;
import com.tablero.repository.TaskBoardDaoImpl;
import com.tablero.repository.TaskDao;
import com.tablero.repository.TaskDaoImpl;
import com.tablero.repository.UserDao;
import com.tablero.repository.UserDaoImpl;
import com.tablero.service.AuthService;
import com.tablero.service.BoardService;
import com.tablero.service.TaskService;
import com.tablero.service.UserService;
import com.tablero.utils.PasswordUtil;

import io.javalin.Javalin; // Esto es para  usar el servidor embebido de H2

public class App {
    public static void main(String[] args) {
        System.out.println("AAPPPP STARRRTSS!");

        // inicializar la base de datos
        try {
            DatabaseConfig.initialize();
            System.out.println("Base de datos inicializada.");
        } catch (SQLException e) {
            System.out.println("Error inicializando DB: " + e.getMessage());
        }

        UserDao userDao = new UserDaoImpl();
        TaskBoardDao taskBoardDao = new TaskBoardDaoImpl();
        TaskDao taskDao = new TaskDaoImpl();
        AuthService authService = new AuthService(userDao);
        UserService userService = new UserService(userDao);
        BoardService boardService = new BoardService(taskBoardDao, userDao);
        TaskService taskService = new TaskService(taskDao, userDao);
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        BoardController boardController = new BoardController(boardService);
        TaskController taskController = new TaskController(taskService);

        // agregar admin si no existe
        if (userDao.findByName("superAdmin").isEmpty()) {
            System.out.println("Admin has not been created yet");
            String password = "123456";
            User admin = new User("superAdmin", PasswordUtil.hashPassword(password), Role.ADMIN);
            userDao.save(admin);
            System.out.println("Admin Created: " + admin.getUsername());
        }

        // 3. Levantar el servidor web de H2 en un puerto aparte HECHO CIN IA, GRACIAS
        // COPILOT
        try {
            Server h2WebServer = Server.createWebServer(
                    "-webAllowOthers", // permite acceso desde fuera de localhost
                    "-webPort", "8082" // puerto de la consola web
            ).start();
            System.out.println("H2 Console running at: " + h2WebServer.getURL());
        } catch (SQLException e) {
            System.out.println("Error iniciando H2 Console: " + e.getMessage());
        }

        // arranca el servidor de javalin
        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.http.defaultContentType = "application/json";
            config.staticFiles.add("/public");
        }).start(7000);

        RouteConfig routeConfig = new RouteConfig(authController, userController, boardController, taskController);

        routeConfig.registerRoutes(app);

    }
}
