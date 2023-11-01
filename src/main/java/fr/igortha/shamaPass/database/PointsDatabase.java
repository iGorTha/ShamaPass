package fr.igortha.shamaPass.database;


import fr.igortha.shamaPass.Main;
import fr.igortha.shamaPass.utils.Logger;
import org.bukkit.entity.Player;

import java.sql.*;

public class PointsDatabase {
    private final Connection connection;

    public PointsDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL," +
                    "points INTEGER NOT NULL DEFAULT 0," +
                    "levels INTEGER NOT NULL DEFAULT 0)");
            Main.getInstance().getLogger().info("Database connected!");
        }
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
            Main.getInstance().getLogger().info("Database connection closed!");
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        if (!playerExists(player)) {
            try (PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT OR IGNORE INTO players (uuid, username) VALUES (?, ?)")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getDisplayName());
                preparedStatement.executeUpdate();
            }
        }
    }

    public void addPoints(Player player, Player target, int points) throws SQLException {
        if (!this.playerExists(target)) {
            this.addPlayer(target);
        }

        int currentPoints = getPoint(player, target);
        int newPoints = currentPoints + points;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE players SET points = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, newPoints);
            preparedStatement.setString(2, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
        Logger.send(player, "Vous venez d'ajouter " + points + " points à " + target.getName());
    }
    public void removePoints(Player player, Player target, int points) throws SQLException {
        if (!this.playerExists(target)) {
            Logger.send(player, "Player not found!");
            return;
        }

        int currentPoints = this.getPoint(player, target);
        int newPoints = currentPoints - points;

        if (newPoints < 0) {
            Logger.send(player, "Le joueur passerait en négatif si vous lui retirez des points !");
            return;
        }

        try (PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE players SET points = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, newPoints);
            preparedStatement.setString(2, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
        Logger.send(player, "Vous venez d'enlever " + points + " points à " + target.getName());
    }

    public int getPoint(Player player, Player target) throws SQLException {
        if (!this.playerExists(target)) {
            Logger.send(player, "Player not found!");
            return 0;
        }

        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT points FROM players WHERE uuid =?")) {
            preparedStatement.setString(1, target.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("points");
            } else {
                return 0;
            }
        }
    }

    public int getLevel(Player player, Player target) throws SQLException {
        if (!this.playerExists(target)) {
            Logger.send(player, "Player not found!");
            return 0;
        }

        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT levels FROM players WHERE uuid =?")) {
            preparedStatement.setString(1, target.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("levels");
            } else {
                return 0;
            }
        }
    }
}
