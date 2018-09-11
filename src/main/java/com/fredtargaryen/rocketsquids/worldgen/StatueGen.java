package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import static com.fredtargaryen.rocketsquids.block.BlockStatue.ACTIVATION;

public class StatueGen implements IWorldGenerator {
    /**
     * Generate some world
     *
     * @param random the chunk specific {@link Random}.
     * @param chunkX the chunk X coordinate of this chunk.
     * @param chunkZ the chunk Z coordinate of this chunk.
     * @param world : additionalData[0] The minecraft {@link World} we're generating for.
     * @param chunkGenerator : additionalData[1] The {@link IChunkProvider} that is generating.
     * @param chunkProvider : additionalData[2] {@link IChunkProvider} that is requesting the world generation.
     *
     */
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        StatueManager statueManager = StatueManager.forWorld(world);
        int chunkAreaX = chunkX / 100;
        int chunkAreaZ = chunkZ / 100;
        int[] statueLocation = statueManager.getChunkArea(chunkAreaX, chunkAreaZ);
        if(statueLocation == null) {
            //A statue location hasn't been decided for this chunk area. Decide one
            statueLocation = new int[] {chunkAreaX, chunkAreaZ,
                    //Random chunk in the 100x100 area
                    (chunkAreaX * 100 + random.nextInt(100))
                    //Random block in the 16x16 chunk
                            * 16 + random.nextInt(16),
                    random.nextInt(80) + 1,
                    (chunkAreaZ * 100 + random.nextInt(100)) * 16 + random.nextInt(16)};
            statueManager.addStatue(new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]));
        }
        if(chunkX == statueLocation[2] / 16 && chunkZ == statueLocation[4] / 16) {
            //The statue should go in this chunk. Put a statue in here
            BlockPos placePos = new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]);
            world.setBlockState(placePos.up(), Blocks.AIR.getDefaultState());
            IBlockState state = RocketSquidsBase.blockStatue.getDefaultState();
            world.setBlockState(placePos, state);
            RocketSquidsBase.blockStatue.updateTick(world, placePos, state, random);
        }
    }
}
