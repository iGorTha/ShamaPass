package fr.igortha.shamaPass.commands;

import fr.igortha.shamaPass.Main;
import fr.igortha.shamaPass.utils.Logger;
import fr.igortha.shamaPass.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class XpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Logger.send(sender, Main.getInstance().getConfig().getString("messages.command-error"));
            return true;
        }

        if (!Permissions.hasPermission(player, Permissions.COMMAND_XP)) {
            Logger.send(player, Main.getInstance().getConfig().getString("messages.command-permission"));
            return true;
        }

        if (args.length >= 2) {
            String playerName = args[0];
            Player targetPlayer = Bukkit.getPlayerExact(playerName);

            if (targetPlayer == null) {
                Logger.send(player, Main.getInstance().getConfig().getString("messages.found-player"));
                return true;
            }

            if (args[1].equalsIgnoreCase("addpoints")) {
                if (args.length == 3) {
                    String arg2 = args[2];
                    try {
                        int points = Integer.parseInt(arg2);
                        Main.getInstance().getPointsDatabase().addPoints(player, targetPlayer, points);
                    } catch (NumberFormatException e) {
                        Logger.send(player, Main.getInstance().getConfig().getString("messages.args-number"));
                    }
                }
            } else if (args[1].equalsIgnoreCase("removepoints")) {
                if (args.length == 3) {
                    String arg2 = args[2];
                    try {
                        int points = Integer.parseInt(arg2);
                        Main.getInstance().getPointsDatabase().removePoints(player, targetPlayer, points);
                    } catch (NumberFormatException e) {
                        Logger.send(player, Main.getInstance().getConfig().getString("messages.args-number"));
                    }
                }
            } else if (args[1].equalsIgnoreCase("getpoints")) {
                if (!Main.getInstance().getPointsDatabase().playerExists(targetPlayer)) {
                    Logger.send(player, Main.getInstance().getConfig().getString("messages.found-player"));
                    return false;
                }
                int numberPoints = Main.getInstance().getPointsDatabase().getPoint(targetPlayer);
                Logger.send(player, Main.getInstance().getConfig().getString("messages.number-xp")
                        .replace("player", targetPlayer.getName())
                        .replace("{xp}", String.valueOf(numberPoints))
                );
            } else if (args[1].equalsIgnoreCase("getLevels")) {
                if (!Main.getInstance().getPointsDatabase().playerExists(targetPlayer)) {
                    Logger.send(player, Main.getInstance().getConfig().getString("messages.found-player"));
                    return false;
                }
                int numberLevels = Main.getInstance().getPointsDatabase().getLevel(targetPlayer);
                Logger.send(player, Main.getInstance().getConfig().getString("messages.number-level")
                        .replace("{player}", targetPlayer.getName())
                        .replace("{level}", String.valueOf(numberLevels))
                );
            } else {
                Logger.send(player, Main.getInstance().getConfig().getString("messages.use-command"));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String valueToComplete = args[0].toLowerCase();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String playerName = onlinePlayer.getName().toLowerCase();

                if (playerName.startsWith(valueToComplete)) {
                    completions.add(onlinePlayer.getName());
                    Main.getInstance().getLogger().severe("Player : " + playerName);
                }
            }
        } else if (args.length == 2) {
            completions.add("addpoints");
            completions.add("removepoints");
            completions.add("getpoints");
            completions.add("getLevels");
        }
        return completions;
    }
}
