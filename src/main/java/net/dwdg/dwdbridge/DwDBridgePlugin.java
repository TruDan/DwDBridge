package net.dwdg.dwdbridge;

import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import net.dwdg.dwdbridge.listeners.PlayerListener;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dan
 */
public class DwDBridgePlugin extends JavaPlugin {

    private static DwDBridgePlugin plugin;
    public static Permission permission = null;

    private Database xenConnection;

    public void onEnable() {
        DwDBridgePlugin.plugin = this;
        setupPermissions();

        xenConnection = new MySQL(Logger.getLogger("Minecraft"), "[DwDBridge] ", getConfig().getString("xenConfig.host", "localhost"), getConfig().getInt("xenConfig.port", 3306), getConfig().getString("xenConfig.db", "xenforo"), getConfig().getString("xenConfig.user", "root"), getConfig().getString("xenConfig.pass", ""));
        
        if (!xenConnection.open()) {
            Bukkit.getPluginManager().disablePlugin(this);
            System.out.println("Connection Failed. DwDBridge Disabled.");
        }
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new DwDPlayers(), plugin);
        
        System.out.println("Connection Succeeded. DwDBridge Ready.");
    }

    public void onDisable() {
        xenConnection.close();
        System.out.println("Connection Closed. DwDBridge Disabled.");
    }

    public void onCommand() {

    }
    
    
    
    public static DwDBridgePlugin getPlugin() {
        return plugin;
    }
    
    public Database getDb() {
        return xenConnection;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

}
