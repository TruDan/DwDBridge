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
        if (pCheck.validate()) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Ask for confirmation
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            DwDBridgePlugin.getPlugin().getConfig().getString("messages.askConfirmation")
                            .replaceAll("%N", pCheck.getXenUsername())
                    ));
                } else {
                    // Hasnt tied forums account
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', DwDBridgePlugin.getPlugin().getConfig().getString("messages.errorGettingInfo")));
                }
            } else {
                // Check & Sync rank
                
            }
        } else {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', DwDBridgePlugin.getPlugin().getConfig().getString("messages.errorGettingInfo")));
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        DwDPlayer pCheck = new DwDPlayer(event.getPlayer());

        if (event.getMessage().startsWith("/confirm")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(true);
                    event.setCancelled(true);
                }
            }
        } else if (event.getMessage().startsWith("/deny")) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Confirm
                    pCheck.setMcConfirmed(false);
                    pCheck.removeEntry();
                    event.setCancelled(true);
                }
            }
        }
    }
}
