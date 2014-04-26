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

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoin(PlayerJoinEvent event) {
        DwDPlayer pCheck = DwDPlayers.getPlayer(event.getPlayer().getUniqueId());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        if (pCheck.validate()) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Ask for confirmation
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.askConfirmation")
                            .replaceAll("%N", pCheck.getXenUsername())
                    ));
                } else {
                    // Hasnt tied forums account
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.errorGettingInfo")));
                }
            } else {
                // Check & Sync rank

                rankSync(event.getPlayer());

            }
        } else {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.errorGettingInfo")));
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        DwDPlayer pCheck = DwDPlayers.getPlayer(event.getPlayer().getUniqueId());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        if (event.getMessage().toLowerCase().startsWith("/confirm") || event.getMessage().equalsIgnoreCase("/confirm")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(true);
                    rankSync(event.getPlayer());
                    
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

    private void rankSync(Player player) {
        DwDPlayer pCheck = DwDPlayers.getPlayer(player.getUniqueId());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();
        
        // Remove ALL Groups
        String[] groups = DwDBridgePlugin.permission.getPlayerGroups(player);

        for (String group : groups) {
            DwDBridgePlugin.permission.playerRemoveGroup(player, group);
        }

        // Re-Add the correct groups
        String ranksAdded = "";
        int pgId = pCheck.getPrimaryGroup();
        String pgName = plugin.getConfig().getString("rankSync." + pgId);
        if (pgName != null) {
            DwDBridgePlugin.permission.playerAddGroup(player, pgName);
            ranksAdded += " " + pgName;
        }

        for (int gId : pCheck.getSecondaryGroups()) {
            String gName = plugin.getConfig().getString("rankSync." + gId);
            if (gName != null) {
                DwDBridgePlugin.permission.playerAddGroup(player, gName);
                ranksAdded += " " + gName;
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.ranksAdded")
                .replaceAll("%N", pCheck.getXenUsername())
                .replaceAll("%R", ranksAdded)
        ));
    }
}
