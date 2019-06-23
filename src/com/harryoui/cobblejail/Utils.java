package com.harryoui.cobblejail;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {

    public static boolean verboseFlag;

    public static FileConfiguration config = new YamlConfiguration();

    public static File ConfigPath = new File(CobbleJail.getPlugin().getDataFolder(), File.separator + "config.yml"); //stores plugin wide variables

    public static void Log(String name, String msg) {
        if (verboseFlag) {
            CobbleJail.getPlugin().getLogger().info("[" + name + "] " + msg);
        }
    }

    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public static void SetupDataFiles() {
        if (!ConfigPath.exists()) {
            CobbleJail.getPlugin().saveDefaultConfig();
        }
    }

    public static void loadConfig() {
        config = YamlConfiguration.loadConfiguration(ConfigPath);
        verboseFlag = config.getBoolean("Verbose");
        CobbleJail.getPlugin().getLogger().info("[General] Logging mode is " + verboseFlag);
    }

    public static MultiverseCore getMultiverse() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");

        // Multiverse may not be loaded
        if (plugin == null || !(plugin instanceof MultiverseCore)) {
            return null; // Maybe you want throw an exception instead
        }

        return (MultiverseCore) plugin;
    }

    public static void test() {

    }

    public static Player getPlayerFromUUID(UUID UUID) { //returns null if player is offline
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(UUID)) {
                return p;
            }
        }
        return null;
    }

    public static Player getPlayerByName(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name) || p.getCustomName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public static String getJailWorldName() {
        return "world_jail";
    }

    public static World getJailWorld() {
        return Bukkit.getWorld(getJailWorldName());
    }

    public static World getOverworld() {
        return Bukkit.getWorld("world");
    }

    public static UUID toUUID(String notUUID) {
        return java.util.UUID.fromString(notUUID);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatDouble(double inp) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(inp);
    }

    static void kickAllFromJail() {
        for (Player p : Utils.getJailWorld().getPlayers()) {
            p.kickPlayer("Jail is shutting down");
        }
    }
}
