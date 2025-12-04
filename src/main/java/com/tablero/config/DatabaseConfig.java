package com.tablero.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:h2:./data/tablero_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initialize() throws SQLException {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS USERS("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "username varchar(255) NOT NULL,"
                    + "password_hash varchar(255) NOT NULL,"
                    + "role varchar(10) NOT NULL"
                    + ")";

            stmt.execute(createUsersTable);

            String createTaskBoardsTable = "CREATE TABLE IF NOT EXISTS TASKBOARDS("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(255)"
                    + ")";

            stmt.execute(createTaskBoardsTable);

            String createRelTable = "CREATE TABLE IF NOT EXISTS USERS_TASKBOARDS ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id int NOT NULL,"
                    + "board_id int NOT NULL,"
                    + "FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (board_id) REFERENCES TASKBOARDS(id) ON DELETE CASCADE"
                    + ")";

            stmt.execute(createRelTable);

            String createTasksTable = "CREATE TABLE IF NOT EXISTS TASKS("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "description VARCHAR(255) NOT NULL,"
                    + "state VARCHAR(50) NOT NULL,"
                    + "board_id INT NOT NULL,"
                    + "FOREIGN KEY (board_id) REFERENCES TASKBOARDS(id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createTasksTable);
        } catch (Exception e) {
            System.out.println("ERROR AL INICIAR LA DB");
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
