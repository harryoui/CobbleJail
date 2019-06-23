package com.harryoui.cobblejail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class JailStorage {

    public static File JailedPath = new File(CobbleJail.getPlugin().getDataFolder(), File.separator + "jail.yml");
    public static FileConfiguration jailYaml;
    public static Map<UUID, JailEntry> jailData = new HashMap<>();

    public static class JailEntry {

        private final UUID uuid;
        private int quota;
        private int progress;

        private JailEntry(UUID uuid, int quota, int progress) {
            this.uuid = uuid;
            this.quota = quota;
            this.progress = progress;
        }

        double mined() {
            progress += 1;
            return ((double) progress / (double) quota);
        }

        double setQuota(int quota) {
            this.quota = quota;
            return progress / quota;
        }
    }

    static double playerMined(Player player) {
        double progress = jailData.get(player.getUniqueId()).mined();

        String bar = "";
        int counter = 10;
        double tempProgress = progress;
        while (counter > 0) {
            if (tempProgress > 0) {
                bar += ChatColor.GREEN + "▰";
            } else {
                bar += ChatColor.GRAY + "▱";
            }
            tempProgress -= 0.10;
            counter--;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utils.formatDouble(progress * 100) + "%"));
        return progress;
    }

    static void newEntry(UUID uuid, int Quota, int Progress) {
        jailData.put(uuid, new JailEntry(uuid, Quota, Progress));
    }

    static void removeEntry(UUID uuid) {
        jailData.remove(uuid);
    }

    static boolean containsEntry(UUID uuid) {
        return jailData.containsKey(uuid);
    }

    static void saveAll() { //wipe yaml and load it from array
        wipeYaml();

        ConfigurationSection rootSection = jailYaml.getConfigurationSection("Players");
        for (JailEntry e : jailData.values()) {
            String UUID = e.uuid.toString();

            rootSection.createSection(UUID);
            ConfigurationSection section = rootSection.getConfigurationSection(UUID);
            section.set("Quota", e.quota);
            section.set("Progress", e.progress);
        }
        try {
            JailStorage.jailYaml.save(JailedPath);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void loadYaml() { //wipe storage array and load it from file
        jailData.clear();
        if (!JailStorage.JailedPath.exists()) {
            try {
                JailStorage.JailedPath.createNewFile();
                JailStorage.jailYaml = YamlConfiguration.loadConfiguration(JailStorage.JailedPath);
                JailStorage.jailYaml.createSection("Players");
                jailYaml.save(JailedPath);
            } catch (IOException ex) {
                Logger.getLogger(JailStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JailStorage.jailYaml = YamlConfiguration.loadConfiguration(JailStorage.JailedPath);
        }
        
        ConfigurationSection rootSection = jailYaml.getConfigurationSection("Players");

        if (rootSection != null) {
            for (String sectionStr : rootSection.getKeys(false)) {
                //ConfigurationSection section = jailYaml.getConfigurationSection(sectionStr);

                UUID UUID = Utils.toUUID(sectionStr);
                int Quota = rootSection.getInt(sectionStr + "." + "Quota");
                int Progress = rootSection.getInt(sectionStr + "." + "Progress");
                newEntry(UUID, Quota, Progress);
            }
        }
    }

    static void wipeYaml() {
        for (String s : jailYaml.getKeys(false)) { //delete contents of yaml
            jailYaml.set(s, null);
        }
        jailYaml.createSection("Players");
    }
}
