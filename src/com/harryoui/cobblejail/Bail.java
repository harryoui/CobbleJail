package com.harryoui.cobblejail;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Bail {

    static void bail(Player player) {
        if (player.teleport(Utils.getOverworld().getSpawnLocation())) {
            JailStorage.removeEntry(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have met your quota and have been freed");
        } else {
            CobbleJail.getPlugin().getLogger().severe("Could not teleport " + player.getName() + " to jail!");
        }
    }
}
