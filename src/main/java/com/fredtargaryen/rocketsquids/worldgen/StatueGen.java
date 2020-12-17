package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class StatueGen extends Feature<StatueGenConfig> {

    public StatueGen(Codec<StatueGenConfig> codec) {
        super(codec);
    }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     * @param world
     * @param chunkGen
     * @param random
     * @param pos
     * @param config
     * @return
     */
    @Override
    public boolean generate(ISeedReader world, ChunkGenerator chunkGen, Random random, BlockPos pos, StatueGenConfig config) {
        // First check the config to see if this dimension is allowed
        if(GeneralConfig.STATUE_USE_WHITELIST.get())
        {
            List<? extends String> allowedDimensions = GeneralConfig.STATUE_WHITELIST.get();
            if(!allowedDimensions.contains(world.getWorld().getDimensionKey().getLocation().toString())) return false;
        }
        else
        {
            List<? extends String> blockedDimensions = GeneralConfig.STATUE_BLACKLIST.get();
            if(blockedDimensions.contains(world.getWorld().getDimensionKey().getLocation().toString())) return false;
        }
        StatueManager statueManager = StatueManager.forWorld(world.getWorld());
        int chunkX = pos.getX() / 16;
        int chunkZ = pos.getZ() / 16;
        int chunkAreaX = chunkX / DataReference.CHUNK_AREA_SIZE;
        int chunkAreaZ = chunkZ / DataReference.CHUNK_AREA_SIZE;
        int[] statueLocation = statueManager.getChunkArea(chunkAreaX, chunkAreaZ);
        if(statueLocation == null) {
            //A statue location hasn't been decided for this chunk area. Decide one
            statueLocation = new int[] {chunkAreaX, chunkAreaZ,
                    //Random chunk in the sizexsize area
                    (chunkAreaX * DataReference.CHUNK_AREA_SIZE + random.nextInt(DataReference.CHUNK_AREA_SIZE))
                            //Random block in the 16x16 chunk
                            * 16 + random.nextInt(16),
                    random.nextInt(254) + 1,
                    (chunkAreaZ * DataReference.CHUNK_AREA_SIZE + random.nextInt(DataReference.CHUNK_AREA_SIZE))
                            * 16 + random.nextInt(16)};
            statueManager.addStatue(new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]));
        }
        if(chunkX == statueLocation[2] / 16 && chunkZ == statueLocation[4] / 16) {
            //The statue should go in this chunk. Put a statue in here
            BlockPos placePos = new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]);
            //Simulate the block falling down onto a solid block
            statueManager.removeStatue(placePos);
            BlockPos pos2;
            for(pos2 = placePos; !world.getBlockState(pos2.down()).getMaterial().isSolid(); pos2 = pos2.down());
            world.setBlockState(pos2.up(), Blocks.AIR.getDefaultState(), 3);
            world.setBlockState(pos2, RocketSquidsBase.BLOCK_STATUE.getDefaultState(), 3);
            statueManager.addStatue(pos2);
            return true;
        }
        return false;
    }
}
