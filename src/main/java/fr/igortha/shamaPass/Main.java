package fr.igortha.shamaPass;

import fr.igortha.shamaPass.commands.XpCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

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
        this.loadCommand();
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
