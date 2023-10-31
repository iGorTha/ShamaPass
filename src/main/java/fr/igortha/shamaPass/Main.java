package fr.igortha.shamaPass;

import fr.igortha.shamaPass.commands.XpCommand;
import fr.igortha.shamaPass.database.PointsDatabase;
import fr.igortha.shamaPass.placeholder.XpPassPlaceHolder;
import fr.igortha.shamaPass.placeholder.XpPlaceHolder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {
    //////////////INSTANCE///////////////////
    @Getter
    private PointsDatabase pointsDatabase;
    @Getter
    private static Main instance;

    /////////////////////////////////////////
    @Override
    public void onLoad() {
        instance = this;
        this.getLogger().info("ShamaPass loaded!");
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (!instance.getConfig().getBoolean("config.enabled-plugin")) {
            this.getLogger().severe("The plugin has been deactivated in the config, it will not be start on the server.");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getLogger().severe("Could not find PlaceholderAPI! This plugin is required.");
            this.setEnabled(false);
            return;
        }
        ///////////////////CONNECT_DB//////////////////////////
        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdirs();

            this.pointsDatabase = new PointsDatabase(getDataFolder().getAbsolutePath() + "/points.db");
        } catch (SQLException exception) {
            exception.printStackTrace();
            this.getLogger().severe("Failed to connect to the dartabase! " + exception.getSQLState());
            this.setEnabled(false);
            return;
        }

        //////////////////////////////////////////////////////

        //////////////////REGISTER_COMMAND////////////////////
        this.loadCommand();
        //////////////////////////////////////////////////////

        ///////////////////PLACEHOLDER////////////////////////
        new XpPassPlaceHolder().register();
        new XpPlaceHolder().register();
        //////////////////////////////////////////////////////

        this.getLogger().info("ShamaPass enabled!");
    }

    @Override
    public void onDisable() {
        ///////////////////DISCONNECT_DB//////////////////////
        try {
            this.pointsDatabase.closeConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        //////////////////////////////////////////////////////

        this.getLogger().info("ShamaPass disabled!");
    }

    public void loadCommand() {
        getCommand("xp").setExecutor(new XpCommand());
        this.getLogger().info("ShamaPass loaded commands!");
    }
}
