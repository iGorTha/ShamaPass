package fr.igortha.shamaPass.db;

import fr.igortha.shamaPass.Main;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    @Getter
    private Connection connection;
    public String host;
    public String database;
    public String username;
    public String password;
    public int port;

    public void mysqlSetup() {
        this.host = Main.getInstance().getConfig().getString("config.db-connect.host");
        this.port = Main.getInstance().getConfig().getInt("config.db-connect.port");
        this.database = Main.getInstance().getConfig().getString("config.db-connect.database");
        this.username = Main.getInstance().getConfig().getString("config.db-connect.username");
        this.password = Main.getInstance().getConfig().getString("config.db-connect.password");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));

                Main.getInstance().getLogger().info("Connected to MySQL");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
