package com.wizm.msg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class MessageSender {

    private final WizmMsg plugin;

    public MessageSender(WizmMsg plugin) {
        this.plugin = plugin;
    }

    public void sendPrivate(Player sender, String targetName, String message) {
        ConfigManager cm = plugin.getConfigManager();

        String fromMsg = cm.format("from", sender.getName(), targetName, message);
        String toMsg   = cm.format("to",   sender.getName(), targetName, message);

        sender.sendMessage(fromMsg);

        if (plugin.getProxyDetector().isProxy()) {
            sendCrossServer(sender, targetName, toMsg);
        } else {
            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null && target.isOnline()) {
                target.sendMessage(toMsg);
            } else {
                sender.sendMessage(cm.getMessage("player-not-found"));
            }
        }
    }

    private void sendCrossServer(Player sender, String targetName, String formatted) {
        String channel = plugin.getConfigManager().getString("settings.proxy-channel");
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Message");
            out.writeUTF(targetName);
            out.writeUTF(formatted);
            sender.sendPluginMessage(plugin, channel, b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}