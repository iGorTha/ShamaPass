package fr.igortha.shamaPass.utils;

import fr.igortha.shamaPass.Main;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public enum Permissions {
    COMMAND_XP(Main.getInstance().getConfig().getString("permissions.command-xp")),
    ;

    private final String perm;

    public static final String BASE_PERMISSION = Main.getInstance().getConfig().getString("permissions.base-permissions");

    Permissions(String perm) {
        this.perm = perm;
    }

    public static boolean hasPermission(CommandSender sender, Permissions perm) {
        return sender.hasPermission(BASE_PERMISSION + perm);
    }
}
