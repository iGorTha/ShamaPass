package fr.igortha.shamaPass.database;

import fr.igortha.shamaPass.Main;
import fr.igortha.shamaPass.utils.Logger;
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
                preparedStatement.setString(2, player.getName());
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

        ///////////////////VARIABLES/////////////////////////
        int currentPoints = getPoint(target);
        int maxXP = Main.getInstance().getConfig().getInt("config.max-xp");
        int currentLevel = getLevel(target);
        int newLevel = currentLevel;
        /////////////////////////////////////////////////////

        int remainingPoints = points;

        if (currentPoints + remainingPoints >= maxXP) {
            Logger.send(player, Main.getInstance().getConfig().getString("messages.over-the-limit")
                    .replace("maxXP", String.valueOf(maxXP)
                    ));
            return;
        }

        while (remainingPoints > 0) {
            if (currentPoints + remainingPoints >= (newLevel + 1) * 100) {
                int pointsToAdd = ((newLevel + 1) * 100) - currentPoints;
                currentPoints += pointsToAdd;
                remainingPoints -= pointsToAdd;
                newLevel++;
            } else {
                currentPoints += remainingPoints;
                remainingPoints = 0;
            }
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE players SET points = ?, levels = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, currentPoints);
            preparedStatement.setInt(2, newLevel);
            preparedStatement.setString(3, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Logger.send(player, Main.getInstance().getConfig().getString("messages.add-xp")
                .replace("{xp}", String.valueOf(points))
                .replace("{player}", target.getName())
        );
        if (newLevel > currentLevel) {
            Logger.send(target, Main.getInstance().getConfig().getString("messages.level-up")
                    .replace("{player}", target.getName())
                    .replace("{level}", String.valueOf(newLevel)
                    ));
        }
    }

    public void removePoints(Player player, Player target, int points) {
        if (!playerExists(target)) {
            Logger.send(player, Main.getInstance().getConfig().getString("messages.found-player"));
            return;
        }

        int currentPoints = getPoint(target);
        int newPoints = currentPoints - points;

        int newLevel = getLevel(target);
        int oldLevel = newLevel;

        while (newPoints < newLevel * 100) {
            newLevel--;
            if (newLevel < 0) {
                newLevel = 0;
                break;
            }
        }

        if (newPoints < 0) {
            Logger.send(player, Main.getInstance().getConfig().getString("messages.below-the-limit"));
            return;
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE players SET points = ?, levels = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, newPoints);
            preparedStatement.setInt(2, newLevel);
            preparedStatement.setString(3, target.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Logger.send(player, Main.getInstance().getConfig().getString("messages.remove-xp").replace("{xp}", String.valueOf(points)));

        if (oldLevel != newLevel) {
            Logger.send(target, Main.getInstance().getConfig().getString("messages.level-down")
                    .replace("{level}", String.valueOf(newLevel)
                    ));
        }
    }

    public int getPoint(Player target) {
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

    public int getLevel(Player target) {
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
