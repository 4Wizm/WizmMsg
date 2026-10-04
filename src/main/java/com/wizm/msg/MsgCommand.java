package com.wizm.msg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MsgCommand implements CommandExecutor, TabCompleter {

    private final WizmMsg plugin;

    public MsgCommand(WizmMsg plugin) {
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
            player.sendMessage(cm.getMessage("usage-msg"));
            return true;
        }

        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
            if (!player.hasPermission("wizmmsg.use.lockmessage")) {
                player.sendMessage(cm.getMessage("no-permission"));
                return true;
            }
            boolean block = args[0].equalsIgnoreCase("off");
            plugin.getDatabaseManager().setBlocked(player.getName(), block);
            player.sendMessage(cm.getMessage(block ? "toggled-off" : "toggled-on"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("wizmmsg.use.reload")) {
                player.sendMessage(cm.getMessage("no-permission"));
                return true;
            }
            plugin.reload();
            player.sendMessage(cm.getMessage("reloaded"));
            return true;
        }

        if (args.length < 2) {
            String usage = cm.getMessage("usage-msg").replace("%player%", args[0]);
            player.sendMessage(usage);
            return true;
        }

        String targetName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(cm.getMessage("self-message"));
            return true;
        }

        if (plugin.getDatabaseManager().isBlocked(player.getName())) {
            player.sendMessage(cm.getMessage("self-blocked"));
            return true;
        }

        if (plugin.getDatabaseManager().isBlocked(targetName)) {
            player.sendMessage(cm.getMessage("blocked"));
            return true;
        }

        if (!plugin.getProxyDetector().isProxy()) {
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null || !target.isOnline()) {
                player.sendMessage(cm.getMessage("player-not-found"));
                return true;
            }
        }

        plugin.getDatabaseManager().setLastMessenger(player.getName(), targetName);
        plugin.getMessageSender().sendPrivate(player, targetName, message);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("wizmmsg.use.msg")) return Collections.emptyList();

        List<String> out = new ArrayList<String>();

        if (args.length == 1) {
            String typed = args[0].toLowerCase();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(typed)) {
                    out.add(p.getName());
                }
            }

            if (sender.hasPermission("wizmmsg.use.lockmessage")) {
                if ("on".startsWith(typed)) out.add("on");
                if ("off".startsWith(typed)) out.add("off");
            }
            if (sender.hasPermission("wizmmsg.use.reload")) {
                if ("reload".startsWith(typed)) out.add("reload");
            }
        }

        return out;
    }
}