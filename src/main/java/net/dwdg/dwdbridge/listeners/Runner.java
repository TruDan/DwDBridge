/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dwdg.dwdbridge.listeners;

import net.dwdg.dwdbridge.DwDBridgePlugin;
import net.dwdg.dwdbridge.DwDPlayer;
import net.dwdg.dwdbridge.DwDPlayers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Dan
 */
public class Runner implements Runnable {

    Player p;
    DwDPlayer pCheck;
    
    public Runner(Player p) {
        this.p = p;
        this.pCheck = DwDPlayers.getPlayer(p.getUniqueId());
    }
    
    @Override
    public void run() {
        if (pCheck.validate()) {
            if (!pCheck.isMcConfirmed()) {
                if (pCheck.getXenID() > 0) {
                    // Ask for confirmation
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            DwDBridgePlugin.getPlugin().getConfig().getString("messages.askConfirmation")
                            .replaceAll("%N", pCheck.getXenUsername())
                    ));
                } else {
                    // Hasnt tied forums account
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', DwDBridgePlugin.getPlugin().getConfig().getString("messages.errorGettingInfo")));
                }
            } else {
                // Check & Sync rank

                pCheck.rankSync();

            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', DwDBridgePlugin.getPlugin().getConfig().getString("messages.errorGettingInfo")));
        }
    }

}
