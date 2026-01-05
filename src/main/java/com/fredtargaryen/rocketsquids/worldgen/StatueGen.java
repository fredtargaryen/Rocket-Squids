package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.world.StatueData;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class StatueGen extends Feature<StatueGenConfig> {

    public StatueGen(Codec<StatueGenConfig> codec) {
        super(codec);
    }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     * @param context context
     * @return return
     */
    @Override
    public boolean place(@NotNull FeaturePlaceContext<StatueGenConfig> context) {
        // First we create a few variables out of the context in order to adapt from the old way place was written
        WorldGenLevel world = context.level();
        ChunkGenerator chunkGen = context.chunkGenerator();
        Random random = context.random();
        BlockPos pos = context.origin();
        // Then we check the config to see if this dimension is allowed
        if(GeneralConfig.STATUE_USE_WHITELIST.get())
        {
            List<? extends String> allowedDimensions = GeneralConfig.STATUE_WHITELIST.get();
            if(!allowedDimensions.contains(world.getLevel().dimension().location().toString())) return false;
        }
        else
        {
            List<? extends String> blockedDimensions = GeneralConfig.STATUE_BLACKLIST.get();
            if(blockedDimensions.contains(world.getLevel().dimension().location().toString())) return false;
        }
        StatueData statueManager = StatueData.forWorld(world.getLevel());
        int frequency = GeneralConfig.STATUE_FREQUENCY.get();
        int chunkX = pos.getX() / 16;
        int chunkZ = pos.getZ() / 16;
        int chunkAreaX = chunkX / frequency;
        int chunkAreaZ = chunkZ / frequency;
        int[] statueLocation = statueManager.getChunkArea(chunkAreaX, chunkAreaZ);
        if(statueLocation == null) {
            //A statue location hasn't been decided for this chunk area. Decide one
            statueLocation = new int[] {chunkAreaX, chunkAreaZ,
                    //Random chunk in the sizexsize area
                    (chunkAreaX * frequency + random.nextInt(frequency))
                            //Random block in the 16x16 chunk
                            * 16 + random.nextInt(16),
                    random.nextInt(chunkGen.getGenDepth() - 3) + 1,
                    (chunkAreaZ * frequency + random.nextInt(frequency))
                            * 16 + random.nextInt(16)};
            statueManager.addStatue(new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]));
        }
        if(chunkX == statueLocation[2] / 16 && chunkZ == statueLocation[4] / 16) {
            //The statue should go in this chunk. Put a statue in here
            BlockPos placePos = new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]);
            //Simulate the block falling down onto a solid block
            statueManager.removeStatue(placePos);
            BlockPos pos2;
            for(pos2 = placePos; !world.getBlockState(pos2.below()).getMaterial().isSolid(); pos2 = pos2.below());
            world.setBlock(pos2.above(), Blocks.AIR.defaultBlockState(), 3);
            world.setBlock(pos2, RocketSquidsBase.BLOCK_STATUE.get().defaultBlockState(), 3);
            statueManager.addStatue(pos2);
            return true;
        }
        return false;
    }
}
