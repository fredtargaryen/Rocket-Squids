package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class StatueGen extends Feature<StatueGenConfig> {
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
    public boolean func_212245_a(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGen, Random random, BlockPos pos, StatueGenConfig config) {
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 3);
        IBlockState state = RocketSquidsBase.BLOCK_STATUE.getDefaultState();
        world.setBlockState(pos, state, 3);
        RocketSquidsBase.BLOCK_STATUE.tick(state, (World) world, pos, random);
        return true;
    }
}
