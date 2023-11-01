package fr.igortha.shamaPass.database;


import fr.igortha.shamaPass.Main;
import fr.igortha.shamaPass.utils.Logger;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;

import java.sql.*;
public class PointsDatabase {
    private Connection connection;

    public PointsDatabase() {
        connect();
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + Main.getInstance().getDataFolder().getAbsolutePath() + "/points.db");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL," +
                    "points INTEGER NOT NULL DEFAULT 0," +
                    "levels INTEGER NOT NULL DEFAULT 0)");

            Main.getInstance().getLogger().info("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connect();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            getConnection().close();
            Main.getInstance().getLogger().info("Database connection closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(Player player) {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT 1 FROM players WHERE uuid = ? LIMIT 1")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPlayer(Player player) {
        if (!playerExists(player)) {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT OR IGNORE INTO players (uuid, username) VALUES (?, ?)")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getDisplayName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPoints(Player player, Player target, int points) {
        if (!playerExists(target)) {
            addPlayer(target);
        }

        int currentPoints = getPoint(player, target);
        int newPoints = currentPoints + points;

        int maxPoints = 2147483647;

        if (newPoints >= maxPoints) {
            Logger.send(player, "Vous ne pouvez pas dépasser la limite de point : " + newPoints);
            return;
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE players SET points = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, newPoints);
            preparedStatement.setString(2, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Logger.send(player, "Vous venez d'ajouter " + points + " points à " + target.getName());
    }

    public void removePoints(Player player, Player target, int points) {

        if (!playerExists(target)) {
            Logger.send(player, "Player not found!");
            return;
        }

        int currentPoints = getPoint(player, target);
        int newPoints = currentPoints - points;

        if (newPoints < 0) {
            Logger.send(player, "Le joueur passerait en négatif si vous lui retirez des points !");
            return;
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE players SET points = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, newPoints);
            preparedStatement.setString(2, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Logger.send(player, "Vous venez d'enlever " + points + " points à " + target.getName());
    }

    public int getPoint(Player player, Player target) {
        if (!playerExists(target)) {
            Logger.send(player, "Player not found!");
            return 0;
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT points FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, target.getUniqueId().toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("points") : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getLevel(Player player, Player target) {
        if (!this.playerExists(target)) {
            Logger.send(player, "Player not found!");
            return 0;
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT levels FROM players WHERE uuid =?")) {
            preparedStatement.setString(1, target.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("levels");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
