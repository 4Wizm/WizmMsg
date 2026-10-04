package com.wizm.msg;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final WizmMsg plugin;
    private FileConfiguration cfg;

    public ConfigManager(WizmMsg plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        this.cfg = plugin.getConfig();
    }

    public String getMessage(String key) {
        String raw = cfg.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String format(String key, String sender, String target, String message) {
        String raw = cfg.getString("messages." + key, "");
        raw = raw.replace("%sender%", sender);
        raw = raw.replace("%target%", target);
        raw = raw.replace("%player%", sender);
        raw = raw.replace("%message%", message);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String getString(String path) {
        return cfg.getString(path);
    }

    public int getInt(String path) {
        return cfg.getInt(path);
    }

    public boolean getBoolean(String path) {
        return cfg.getBoolean(path);
    }
}