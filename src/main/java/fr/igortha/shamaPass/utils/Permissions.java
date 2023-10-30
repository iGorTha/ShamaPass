package fr.igortha.shamaPass.utils;

import fr.igortha.shamaPass.Main;
import org.bukkit.command.CommandSender;

public enum Permissions {
    ADMIN(Main.getInstance().getConfig().getString("permissions.admin")),

    ///////////////////////////////
    ///////////COMMAND/////////////
    ///////////////////////////////

    COMMAND_XP(Main.getInstance().getConfig().getString("permissions.command-xp")),
    ;

    public static final String BASE_PERMISSION = Main.getInstance().getConfig().getString("permissions.base-permissions");

    Permissions(String perm) {
    }

    public static boolean hasPermission(CommandSender sender, Permissions perm) {
        return sender.hasPermission(BASE_PERMISSION + perm);
    }
}
