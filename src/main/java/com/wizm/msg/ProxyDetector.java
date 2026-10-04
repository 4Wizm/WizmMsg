package com.wizm.msg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ProxyDetector implements PluginMessageListener, Listener {

    private final WizmMsg plugin;
    private boolean proxy = false;
    private boolean detected = false;

    public ProxyDetector(WizmMsg plugin) {
        this.plugin = plugin;
    }

    public void detect() {
        String channel = plugin.getConfigManager().getString("settings.proxy-channel");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    public boolean isProxy() {
        return proxy;
    }

    public void requestDetection(Player player) {
        if (detected) return;
        String channel = plugin.getConfigManager().getString("settings.proxy-channel");
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GetServers");
            player.sendPluginMessage(plugin, channel, b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        requestDetection(event.getPlayer());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (detected) return;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            DataInputStream din = new DataInputStream(in);
            String sub = din.readUTF();
            if (sub.equalsIgnoreCase("GetServers")) {
                this.proxy = true;
                this.detected = true;
                plugin.getLogger().info("Proxy rilevato. Modalita cross-server attiva.");
            }
        } catch (Exception ignored) {
        }
    }
}