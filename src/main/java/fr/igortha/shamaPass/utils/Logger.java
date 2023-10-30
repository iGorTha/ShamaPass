package fr.igortha.shamaPass.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Logger {

    //////////////////////////////////////
    //send a message with colors
    //Future possibility of creating message formats via enum
    /////////////////////////////////////
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
