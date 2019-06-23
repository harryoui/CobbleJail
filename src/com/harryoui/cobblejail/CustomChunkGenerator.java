package com.harryoui.cobblejail;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class CustomChunkGenerator extends ChunkGenerator {

    

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                int startHeight = 250;
                Material setBlock = Material.STONE;
                
                if (X == 0 || Z == 0 || X == 15 || Z == 15) {
                    setBlock = Material.BARRIER;
                    startHeight = 255;
                }
                
                for (int i = startHeight; i > 0; i--) {
                    chunk.setBlock(X, i, Z, setBlock);
                }
                chunk.setBlock(X, 0, Z, Material.BEDROCK);
            }
        }
        return chunk;
    }
}
