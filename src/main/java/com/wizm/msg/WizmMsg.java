package com.wizm.msg;

import org.bukkit.plugin.java.JavaPlugin;

public class WizmMsg extends JavaPlugin {

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private ProxyDetector proxyDetector;
    private MessageSender messageSender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.connect();
        this.proxyDetector = new ProxyDetector(this);
        this.proxyDetector.detect();
        this.messageSender = new MessageSender(this);

        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("msg").setTabCompleter(new MsgCommand(this));
        getCommand("r").setExecutor(new ReplyCommand(this));

        getServer().getPluginManager().registerEvents(proxyDetector, this);
        getLogger().info("WizmMsg attivato. Modalita proxy: " + proxyDetector.isProxy());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
        getLogger().info("WizmMsg disattivato.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public ProxyDetector getProxyDetector() { return proxyDetector; }
    public MessageSender getMessageSender() { return messageSender; }

    public void reload() {
        reloadConfig();
        configManager.load();
    }
}