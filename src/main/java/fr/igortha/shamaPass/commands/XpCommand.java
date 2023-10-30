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
import java.util.UUID;

public class XpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Logger.send(sender, Main.getInstance().getConfig().getString("messages.command-error"));
            return true;
        }

        Player player = (Player) sender;

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_XP)) {
            Logger.send(player, Main.getInstance().getConfig().getString("messages.command-permission"));
            return true;
        }

        if (args.length == 1) {
            List<String> tabCompletions = onTabComplete(player, cmd, label, args);
            if (tabCompletions.isEmpty()) {
                Logger.send(player, Main.getInstance().getConfig().getString("messages.command-invalid-player"));
                return true;
            }
            String playerName = tabCompletions.get(0);
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) {
                Logger.send(player, Main.getInstance().getConfig().getString("messages.command-found-player"));
                return true;
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String valueToComplete = args[0];
            UUID uuid = null;
            try {
                uuid = UUID.fromString(valueToComplete);
            } catch (IllegalArgumentException ignored) {
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String playerName = onlinePlayer.getName();

                boolean started = playerName.toLowerCase().startsWith(valueToComplete.toLowerCase());
                if ((uuid != null && started) || (uuid == null && started)) {
                    completions.add(playerName);
                }
            }
        }
        return completions;
    }
}
