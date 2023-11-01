package fr.igortha.shamaPass;

import fr.igortha.shamaPass.commands.XpCommand;
import fr.igortha.shamaPass.database.PointsDatabase;
import fr.igortha.shamaPass.placeholder.XpPlaceHolder;
import lombok.Getter;
import me.clip.placeholderapi.libs.kyori.adventure.platform.facet.Facet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        this.pointsDatabase = new PointsDatabase();
        this.pointsDatabase.connect();

        //////////////////////////////////////////////////////

        //////////////////REGISTER_COMMAND////////////////////
        this.loadCommand();
        //////////////////////////////////////////////////////

        ///////////////////PLACEHOLDER////////////////////////
        new XpPlaceHolder().register();
        //////////////////////////////////////////////////////

        this.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8----------------------------------------------------"));
        this.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&aShamaPass enabled!"));
        this.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7Plugin version: " + getDescription().getVersion()));
        this.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7Author: " + getDescription().getAuthors().toString()));
        this.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8----------------------------------------------------"));
    }

    @Override
    public void onDisable() {
        ///////////////////DISCONNECT_DB//////////////////////
        this.pointsDatabase.closeConnection();
        //////////////////////////////////////////////////////

        this.getLogger().info("ShamaPass disabled!");
    }

    public void loadCommand() {
        getCommand("xp").setExecutor(new XpCommand());
        this.getLogger().info("ShamaPass loaded commands!");
    }

}
