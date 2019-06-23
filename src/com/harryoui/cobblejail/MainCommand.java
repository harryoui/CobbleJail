/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harryoui.cobblejail;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 *
 * @author Harry
 */
public class MainCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            if (cmd.getName().equalsIgnoreCase("CobbleJail")) {
                return help(sender);
            }
            if (cmd.getName().equalsIgnoreCase("jail") && args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "help":
                        return help(sender);
                    case "visit":
                        return visit(sender, args);
                    case "leave":
                    case "return":
                        return leave(sender);
                    default:
                        return jail(sender, args);
                }
            } else if (cmd.getName().equalsIgnoreCase("Unjail") && args.length > 0) {
                return unjail(sender, args);
            }
        }
        return false;
    }

    private boolean jail(Player sender, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                Player targetPlayer = Utils.getPlayerByName(args[0]);
                if (targetPlayer != null) {
                    if (args.length == 1) {
                        Commit.commit(targetPlayer, 0, 10);
                    } else if (args.length == 2) {
                        if (Utils.isNumeric(args[1])) {
                            return Commit.commit(targetPlayer, 0, Integer.parseInt(args[1]));
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Not a valid player name!");
                    return true;
                }
            }
        }
        return help(sender);
    }

    private boolean unjail(Player sender, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                Player targetPlayer = Utils.getPlayerByName(args[0]);
                if (targetPlayer != null) {
                    Bail.bail(targetPlayer);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Not a valid player name!");
                }
            }
            return help(sender);
        }
        return help(sender);
    }

    private boolean leave(Player sender) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean visit(Player sender, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static boolean help(Player sender) {
        sender.sendMessage(ChatColor.GRAY + "Jail Commands:");
        if (JailStorage.containsEntry(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.GRAY + "/Jail progress");
        } else {
            sender.sendMessage(ChatColor.GRAY + "/Visit <Player>");
            sender.sendMessage(ChatColor.GRAY + "/Leave");
        }

        if (sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "/Jail <Player> <Stacks>");
            sender.sendMessage(ChatColor.GRAY + "/Unjail <Player>");
        }
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player sender = e.getPlayer();
        if (sender.getLocation().getWorld().equals(Utils.getJailWorld()) && e.getBlock().getType() == Material.STONE) {
            e.setDropItems(false);
            if (JailStorage.containsEntry(sender.getUniqueId())) {
                double percent = JailStorage.playerMined(e.getPlayer());
                if (percent >= 1) {
                    Bail.bail(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player sender = e.getPlayer();
        if (sender.getLocation().getWorld() != Utils.getJailWorld()) {
            if (JailStorage.containsEntry(sender.getUniqueId())) {
                Utils.Log("Commit", "Caught " + sender.getName() + " joining... sent to jail");
                Commit.teleportToJail(sender);
            }
        }
    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getWorld() == Utils.getJailWorld()) {
            Utils.getJailWorld().save(); //ONLY TIME WE SAVE is now. Saving once to try avoid regenerating every server start.
        }
    }
}
