package com.harryoui.cobblejail;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;

public class CobbleJail extends JavaPlugin implements Listener {

    private static Plugin plugin;

    public MainCommand maincommand = new MainCommand();

    @Override
    public void onEnable() {
        plugin = this;

        Utils.registerEvents(this, maincommand);

        Utils.SetupDataFiles();
        Utils.loadConfig();

        JailStorage.loadYaml();

        setupJailDimension();

        getCommand("cobblejail").setExecutor(maincommand);
        getCommand("jail").setExecutor(maincommand);
        getCommand("unjail").setExecutor(maincommand);

        Utils.test();
    }

    @Override
    public void onDisable() {
        //unload and don't save jail world.
        Utils.kickAllFromJail(); //can't unload with player still there
        Bukkit.getServer().unloadWorld(Utils.getJailWorld(), true);
        JailStorage.saveAll();
        plugin = null;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new CustomChunkGenerator();
    }

    public static void setupJailDimension() {
        MVWorldManager manager = Utils.getMultiverse().getMVWorldManager();
        if (!manager.isMVWorld(Utils.getJailWorldName())) {
            Utils.getMultiverse().getMVWorldManager().addWorld(Utils.getJailWorldName(), World.Environment.NORMAL, "", WorldType.FLAT, Boolean.FALSE, CobbleJail.getPlugin().getName(), Boolean.FALSE);
            //world name, environment type, seed, world type, generate structures, custom generator, use spawn adjust
            
            MultiverseWorld mvw = Utils.getMultiverse().getMVWorldManager().getMVWorld(Utils.getJailWorldName());
            mvw.setAutoLoad(false);
            mvw.setAllowAnimalSpawn(false);
            mvw.setAllowMonsterSpawn(false);
            mvw.setHunger(false);
            mvw.setKeepSpawnInMemory(false);
            mvw.setPVPMode(false);
            mvw.setRespawnToWorld(Utils.getJailWorldName());
        }
        Utils.getJailWorld().setAutoSave(false);
    }

}
