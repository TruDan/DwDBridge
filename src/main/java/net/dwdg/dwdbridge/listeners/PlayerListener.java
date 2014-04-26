package net.dwdg.dwdbridge.listeners;

import net.dwdg.dwdbridge.DwDBridgePlugin;
import net.dwdg.dwdbridge.DwDPlayer;
import net.dwdg.dwdbridge.DwDPlayers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Dan
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent event) {

        DwDPlayer pCheck = DwDPlayers.getPlayer(event.getPlayer().getUniqueId());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runner(event.getPlayer()),60L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        DwDPlayer pCheck = DwDPlayers.getPlayer(event.getPlayer().getUniqueId());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        if (event.getMessage().toLowerCase().startsWith("/confirm") || event.getMessage().equalsIgnoreCase("/confirm")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.rankSync();
                    pCheck.setMcConfirmed(true);

                    event.setCancelled(true);
                    event.setMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.confirmed")
                            .replaceAll("%N", pCheck.getXenUsername())));
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.confirmed")
                            .replaceAll("%N", pCheck.getXenUsername())
                    ));
                }
            }
        } else if (event.getMessage().toLowerCase().startsWith("/deny") || event.getMessage().equalsIgnoreCase("/deny")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(false);
                    pCheck.removeEntry();
                    event.setCancelled(true);
                    event.setMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.denied")
                            .replaceAll("%N", pCheck.getXenUsername())));
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.denied")
                            .replaceAll("%N", pCheck.getXenUsername())
                    ));
                }
            }
        }
    }
}
