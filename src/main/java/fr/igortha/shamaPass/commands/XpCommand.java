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
    /*@Override
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
                Logger.send(player, "Le joueur spécifié n'est pas en ligne ou n'existe pas.");
                return true;
            }

            if (args[1].equalsIgnoreCase("addpoints")) {
                if (args.length == 3) {
                    String arg2 = args[2];
                    try {
                        int points = Integer.parseInt(arg2);
                        Main.getInstance().getPointsDatabase().addPoints(player, targetPlayer, points);
                    } catch (NumberFormatException e) {
                        Logger.send(player, "Le troisième argument doit être un nombre entier.");
                    }
                }
            } else if (args[1].equalsIgnoreCase("removepoints")) {
                if (args.length == 3) {
                    String arg2 = args[2];
                    try {
                        int points = Integer.parseInt(arg2);
                        Main.getInstance().getPointsDatabase().removePoints(player, targetPlayer, points);
                    } catch (NumberFormatException e) {
                        Logger.send(player, "Le troisième argument doit être un nombre entier.");
                    }
                }
            } else if (args[1].equalsIgnoreCase("getpoints")) {
                int numberPoints = Main.getInstance().getPointsDatabase().getPoint(player, targetPlayer);
                Logger.send(player, "Le joueur " + targetPlayer.getName() + " a " + numberPoints);
            } else if (args[1].equalsIgnoreCase("getLevels")) {
                int numberLevels = Main.getInstance().getPointsDatabase().getLevel(player, targetPlayer);
                Logger.send(player, "Le joueur " + targetPlayer.getName() + " a " + numberLevels);
            } else {
                Logger.send(player, "Utilisation incorrecte : /commande addpoints | removepoints | getpoints <joueur> <nombre>");
            }
            return true;
        }
        return false;
    }*/
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
                Logger.send(player, "Le joueur spécifié n'est pas en ligne ou n'existe pas.");
                return true;
            }

            String subCommand = args[1].toLowerCase();

            switch (subCommand) {
                case "addpoints":
                case "removepoints":
                    if (args.length == 3) {
                        String arg2 = args[2];
                        try {
                            int points = Integer.parseInt(arg2);
                            if (subCommand.equals("addpoints")) {
                                Main.getInstance().getPointsDatabase().addPoints(player, targetPlayer, points);
                            } else {
                                Main.getInstance().getPointsDatabase().removePoints(player, targetPlayer, points);
                            }
                        } catch (NumberFormatException e) {
                            Logger.send(player, "Le troisième argument doit être un nombre entier.");
                        }
                    }
                    break;
                case "getpoints":
                    int numberPoints = Main.getInstance().getPointsDatabase().getPoint(player, targetPlayer);
                    Logger.send(player, "Le joueur " + targetPlayer.getName() + " a " + numberPoints);
                    break;
                case "getlevels":
                    int numberLevels = Main.getInstance().getPointsDatabase().getLevel(player, targetPlayer);
                    Logger.send(player, "Le joueur " + targetPlayer.getName() + " a " + numberLevels);
                    break;
                default:
                    Logger.send(player, "Utilisation incorrecte : /commande addpoints | removepoints | getpoints | getlevels <joueur> <nombre>");
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
