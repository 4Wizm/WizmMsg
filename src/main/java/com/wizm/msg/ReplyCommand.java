package com.wizm.msg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReplyCommand implements CommandExecutor, TabCompleter {

    private final WizmMsg plugin;

    public ReplyCommand(WizmMsg plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Questo comando si usa solo in gioco.");
            return true;
        }
        Player player = (Player) sender;
        ConfigManager cm = plugin.getConfigManager();

        if (!player.hasPermission("wizmmsg.use.msg")) {
            player.sendMessage(cm.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(cm.getMessage("usage-r"));
            return true;
        }

        if (plugin.getDatabaseManager().isBlocked(player.getName())) {
            player.sendMessage(cm.getMessage("self-blocked"));
            return true;
        }

        String last = plugin.getDatabaseManager().getLastMessenger(player.getName());
        if (last == null || last.equalsIgnoreCase(player.getName())) {
            player.sendMessage(cm.getMessage("no-reply-target"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        if (plugin.getDatabaseManager().isBlocked(last)) {
            player.sendMessage(cm.getMessage("blocked"));
            return true;
        }

        if (!plugin.getProxyDetector().isProxy()) {
            Player target = Bukkit.getPlayerExact(last);
            if (target == null || !target.isOnline()) {
                player.sendMessage(cm.getMessage("player-not-found"));
                return true;
            }
        }

        plugin.getDatabaseManager().setLastMessenger(player.getName(), last);
        plugin.getDatabaseManager().setLastMessenger(last, player.getName());
        plugin.getMessageSender().sendPrivate(player, last, message);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}