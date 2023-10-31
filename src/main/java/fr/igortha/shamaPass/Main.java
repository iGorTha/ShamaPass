package fr.igortha.shamaPass;

import fr.igortha.shamaPass.commands.XpCommand;
import fr.igortha.shamaPass.db.DBConnect;
import fr.igortha.shamaPass.placeholder.XpPassPlaceHolder;
import fr.igortha.shamaPass.placeholder.XpPlaceHolder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private DBConnect dbConnect;

    @Getter
    private static Main instance;
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
        /////////////////////CONNECT_DB///////////////////////
        this.dbConnect = new DBConnect();
        dbConnect.mysqlSetup();
        //////////////////REGISTER_COMMAND////////////////////

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
        this.getLogger().info("ShamaPass disabled!");
    }

    public void loadCommand() {
        getCommand("xp").setExecutor(new XpCommand());
        this.getLogger().info("ShamaPass loaded commands!");
    }
}
