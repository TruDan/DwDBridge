package net.dwdg.dwdbridge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Dan
 */
public class DwDPlayers implements Listener {

    private static Map<UUID, DwDPlayer> dwdPlayers = new HashMap<>();

    public static DwDPlayer getPlayer(UUID uuid) {
        if (dwdPlayers.containsKey(uuid)) {
            return dwdPlayers.get(uuid);
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        dwdPlayers.put(e.getPlayer().getUniqueId(), new DwDPlayer(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        dwdPlayers.get(e.getPlayer().getUniqueId()).save();
        dwdPlayers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent e) {
        dwdPlayers.get(e.getPlayer().getUniqueId()).save();
        dwdPlayers.remove(e.getPlayer().getUniqueId());
    }

}
