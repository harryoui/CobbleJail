package com.harryoui.cobblejail;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commit {

    private static boolean[][] isChunkOccupied = new boolean[101][101]; //primitives asssigned 0 (false) by default
    private static int arrayMiddle = 50; //0+50 = 50 which is middle
    
    static boolean commit(Player targetPlayer, int progress, int stacks) {
        if (teleportToJail(targetPlayer)) {
            targetPlayer.getInventory().clear();

            ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE, 1);
            pickaxe.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
            targetPlayer.getInventory().addItem(pickaxe);

            JailStorage.newEntry(targetPlayer.getUniqueId(), stacks * 64, 0);
            targetPlayer.sendMessage(ChatColor.RED + "You have been incarcerated.");
            targetPlayer.sendMessage(ChatColor.RED + "Mine " + ChatColor.RESET + stacks + ChatColor.RED + " stacks of stone to be set free.");
            return true;
        } else {
            CobbleJail.getPlugin().getLogger().severe("Could not teleport " + targetPlayer.getName() + " to jail!");
        }
        return false;
    }

    static boolean teleportToJail(Player sender) {
        return sender.teleport(getPlayerJail());
    }
    
    private static Location getPlayerJail() {
        //returns a jail world chunk that hasn't been used this restart
        int[] free = findFreeChunk();
        isChunkOccupied[free[0]+arrayMiddle][free[1]+arrayMiddle] = true;
        Location sendLocation = chunkToLocation(free);
        Utils.Log("Commit", "Found a free cell at " + sendLocation.getBlockX() + ", " + sendLocation.getBlockZ());
        return sendLocation;
    }

    private static Location chunkToLocation(int[] chunkPos) {
        double chunkMiddle = 7.5;
        return new Location(Utils.getJailWorld(), 16*chunkPos[0] + chunkMiddle, 255, 16*chunkPos[1] + chunkMiddle);
    }
    static int moves[][] = {{+1, 0}, {0, +1}, {-1, 0}, {0, -1}};

    private static int[] findFreeChunk() {
        if (!isChunkOccupied[0+arrayMiddle][0+arrayMiddle]) { //if 0,0 free, return that
            return new int[]{0, 0};
        }

        //circles around already checked chunks
        int sideLength = 1; //side of already checked chunks cube
        int dirDistanceTravelled = 1;
        int curMove = 0;

        int curPos[] = {-1, -1};

        while (true) {
            if (!isChunkOccupied[curPos[0]+arrayMiddle][curPos[1]+arrayMiddle]) {
                return new int[]{curPos[0], curPos[1]};
            }
            
            if (dirDistanceTravelled == sideLength + 2) { //good for next direction turn
                curMove++;
                dirDistanceTravelled = 1;
                if (curMove == moves.length) { //has reached end of moves, start wider turns
                    sideLength += 2;
                    curMove = 0;
                    curPos[1] += -1; //move to outside of border
                }
            }

            curPos[0] += moves[curMove][0];
            curPos[1] += moves[curMove][1];
            dirDistanceTravelled++;
            
            if (Math.abs(curPos[0]) > 50 || Math.abs(curPos[1]) > 50) {
                Utils.Log("Commit", "ERROR: Infinite loop detected (|Cell Chunk| > 50 X/Z)");
                break;
            }
        }
        
        return new int[]{0, 0};
    }
}
