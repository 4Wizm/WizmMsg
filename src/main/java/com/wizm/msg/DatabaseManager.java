package com.wizm.msg;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final WizmMsg plugin;
    private HikariDataSource dataSource;
    private String table;

    public DatabaseManager(WizmMsg plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        ConfigManager cm = plugin.getConfigManager();
        String host = cm.getString("database.host");
        int port = cm.getInt("database.port");
        String db = cm.getString("database.name");
        String user = cm.getString("database.user");
        String pass = cm.getString("database.password");
        this.table = cm.getString("database.table");

        HikariConfig hc = new HikariConfig();
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        hc.setJdbcUrl(url);
        hc.setUsername(user);
        hc.setPassword(pass);
        hc.setMaximumPoolSize(5);
        hc.setPoolName("WizmMsg-Pool");

        try {
            this.dataSource = new HikariDataSource(hc);
            createTable();
        } catch (Exception e) {
            plugin.getLogger().severe("Errore connessione MySQL: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "player_name VARCHAR(16) PRIMARY KEY, "
                + "last_messenger VARCHAR(16), "
                + "blocked TINYINT(1) DEFAULT 0"
                + ")";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore creazione tabella: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }

    public void setLastMessenger(String player, String target) {
        String sql = "INSERT INTO " + table + " (player_name, last_messenger) VALUES (?,?) "
                + "ON DUPLICATE KEY UPDATE last_messenger=VALUES(last_messenger)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.setString(2, target);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLastMessenger(String player) {
        String sql = "SELECT last_messenger FROM " + table + " WHERE player_name=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("last_messenger");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBlocked(String player, boolean blocked) {
        String sql = "INSERT INTO " + table + " (player_name, blocked) VALUES (?,?) "
                + "ON DUPLICATE KEY UPDATE blocked=VALUES(blocked)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ps.setInt(2, blocked ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlocked(String player) {
        String sql = "SELECT blocked FROM " + table + " WHERE player_name=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("blocked") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}