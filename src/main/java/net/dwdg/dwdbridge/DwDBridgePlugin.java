package net.dwdg.dwdbridge;

import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import net.dwdg.dwdbridge.listeners.PlayerListener;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        saveConfig();

        xenConnection = new MySQL(Logger.getLogger("Minecraft"), "[DwDBridge] ", getConfig().getString("xenConfig.host", "localhost"), getConfig().getInt("xenConfig.port", 3306), getConfig().getString("xenConfig.db", "xenforo"), getConfig().getString("xenConfig.user", "root"), getConfig().getString("xenConfig.pass", ""));

        if (!xenConnection.open()) {
            Bukkit.getPluginManager().disablePlugin(this);
            System.out.println("Connection Failed. DwDBridge Disabled.");
            return;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new DwDPlayers(), this);

        System.out.println("Connection Succeeded. DwDBridge Ready.");
    }

    public void onDisable() {
        xenConnection.close();
        System.out.println("Connection Closed. DwDBridge Disabled.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rank")) {
            if (args.length > 0) {
                if(sender.hasPermission("dwdbridge.others")) {
                    Player[] onlinePlayers = Bukkit.getOnlinePlayers();
                    Player player = null;
                    for(Player p : onlinePlayers) {
                        if(p.getName().startsWith(args[0])) {
                            player = p;
                            break;
                        }
                    }
                    
                    if(player == null) {
                        sender.sendMessage(ChatColor.RED+"That player ins not online.");
                    }
                    
                    DwDPlayer dwdP = DwDPlayers.getPlayer(player.getUniqueId());
                    dwdP.validate();
                    dwdP.rankSync();
                    sender.sendMessage(ChatColor.GREEN+"Syncing that players rank.");
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    DwDPlayer dwdP = DwDPlayers.getPlayer(player.getUniqueId());
                    dwdP.validate();
                    dwdP.rankSync();
                    sender.sendMessage(ChatColor.GREEN+"Your rank is syncronising.");
                }
            }
            return true;
        }
        return false;
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
