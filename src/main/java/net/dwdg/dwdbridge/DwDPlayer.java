package net.dwdg.dwdbridge;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Dan
 */
public class DwDPlayer {

    private Player player;
    private File playerFile;
    private FileConfiguration playerConfig;

    // Forums Info
    private int xenID = 0;
    private String xenUsername = "";
    private int primaryGroupID = 0;
    private ArrayList<Integer> secondaryGroupIDs = new ArrayList<>();
    private int unreadConvos = 0;

    // Internal Variables
    private boolean mcConfirmed = false;

    public DwDPlayer(Player player) {
        this.player = player;

        playerFile = new File(DwDBridgePlugin.getPlugin().getDataFolder(), "data/" + player.getUniqueId().toString() + ".yml");
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        // Check if we already have data for this user
        if (playerFile.exists()) {
            // Load Data
            xenID = playerConfig.getInt("xenID", 0);
            xenUsername = playerConfig.getString("xenUsername", "");
            primaryGroupID = playerConfig.getInt("primaryGroupID", 0);
            List<String> tmpArr = playerConfig.getStringList("secondaryGroupIDs");
            for (String tmp : tmpArr) {
                secondaryGroupIDs.add(Integer.parseInt(tmp));
            }
            mcConfirmed = playerConfig.getBoolean("mcConfirmed", false);

        } else {
            // Create File
            try {
                playerConfig.save(playerFile);
            } catch (Exception e) {
                System.out.println("Error whilst saving data for: " + player.getUniqueId().toString());
                return;
            }
        }
    }

    public boolean validate() {
        DwDBridgePlugin plugin = DwDBridgePlugin.getPlugin();

        String sqlStatement = "SELECT f.`user_id`, u.`username`, u.`user_group_id`, u.`secondary_group_ids`, u.`conversations_unread` FROM `xf_user_field_value` f LEFT JOIN `xf_user` u ON f.`user_id`=u.`user_id` WHERE f.`field_id`='" + plugin.getConfig().getString("uuidField") + "' AND f.`field_value`='" + player.getUniqueId().toString() + "' LIMIT 1";
        ResultSet rS = null;
        try {
            System.out.println(sqlStatement);
            rS = plugin.getDb().query(sqlStatement);

            if (rS.next()) {
                xenID = rS.getInt("user_id");
                xenUsername = rS.getString("username");
                primaryGroupID = rS.getInt("user_group_id");
                unreadConvos = rS.getInt("conversations_unread");

                String secondaryGroupIDString = rS.getString("secondary_group_ids");
                List<String> tmpArr = Arrays.asList(secondaryGroupIDString.split(","));

                for (String tmp : tmpArr) {
                    secondaryGroupIDs.add(Integer.parseInt(tmp));
                }
                save();
                
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public void save() {

        playerConfig.set("xenID", xenID);
        playerConfig.set("xenUsername", xenUsername);
        playerConfig.set("primaryGroupID", primaryGroupID);
        playerConfig.set("secondaryGroupIDs", secondaryGroupIDs);
        playerConfig.set("mcConfirmed", mcConfirmed);

        try {
            playerConfig.save(playerFile);
        } catch (IOException ex) {
        }
    }

    public boolean removeEntry() {
        String sqlStatement = "UPDATE `xf_user_field_value` SET `field_value`='' WHERE `user_id`='" + xenID + "' AND `field_id`='" + DwDBridgePlugin.getPlugin().getConfig().getString("uuidField") + "'";
        try {
            ResultSet rs = DwDBridgePlugin.getPlugin().getDb().query(sqlStatement);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public int getXenID() {
        return xenID;
    }

    public String getXenUsername() {
        return xenUsername;
    }

    public boolean isMcConfirmed() {
        return mcConfirmed;
    }

    public void setMcConfirmed(boolean confirmed) {
        this.mcConfirmed = confirmed;
    }

    public ArrayList<Integer> getSecondaryGroups() {
        return secondaryGroupIDs;
    }

    public Integer getPrimaryGroup() {
        return primaryGroupID;
    }
}
