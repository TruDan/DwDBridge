package net.dwdg.dwdbridge.listeners;

import net.dwdg.dwdbridge.DwDBridgePlugin;
import net.dwdg.dwdbridge.DwDPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Dan
 */
public class PlayerListener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        DwDPlayer pCheck = new DwDPlayer(event.getPlayer());
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

                // Remove ALL Groups
                String[] groups = DwDBridgePlugin.permission.getPlayerGroups(event.getPlayer());

                for (String group : groups) {
                    DwDBridgePlugin.permission.playerRemoveGroup(event.getPlayer(), group);
                }

                // Re-Add the correct groups
                String ranksAdded = "";
                int pgId = pCheck.getPrimaryGroup();
                String pgName = plugin.getConfig().getString("rankSync." + pgId);
                if (pgName != null) {
                    DwDBridgePlugin.permission.playerAddGroup(event.getPlayer(), pgName);
                    ranksAdded += " "+pgName;
                }

                for (int gId : pCheck.getSecondaryGroups()) {
                    String gName = plugin.getConfig().getString("rankSync." + gId);
                    if (gName != null) {
                        DwDBridgePlugin.permission.playerAddGroup(event.getPlayer(), gName);
                    ranksAdded += " "+gName;
                    }
                }
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("messages.ranksAdded")
                        .replaceAll("%N", pCheck.getXenUsername())
                        .replaceAll("%R",ranksAdded)
                ));

            }
        } else {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.errorGettingInfo")));
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        DwDPlayer pCheck = new DwDPlayer(event.getPlayer());
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        if (event.getMessage().startsWith("/confirm")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(true);
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.confirmed")
                            .replaceAll("%N",pCheck.getXenUsername())
                    ));
                }
            }
        } else if (event.getMessage().startsWith("/deny")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(false);
                    pCheck.removeEntry();
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("messages.denied")
                            .replaceAll("%N",pCheck.getXenUsername())
                    ));
                }
            }
        }
    }
}
