package fr.igortha.shamaPass.database;


import fr.igortha.shamaPass.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PointsDatabase {
    private final Connection connection;

    public PointsDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL," +
                    "points INTEGER NOT NULL DEFAULT 0) ");
            Main.getInstance().getLogger().info("Database connected!");
        }
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
            Main.getInstance().getLogger().info("Database connection closed!");
        }

    }

}
