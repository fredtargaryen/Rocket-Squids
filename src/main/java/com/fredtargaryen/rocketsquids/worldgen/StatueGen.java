package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.function.Function;

public class StatueGen extends Feature<StatueGenConfig> {

    public StatueGen(Function<Dynamic<?>, ? extends StatueGenConfig> p_i49878_1_) {
        super(p_i49878_1_);
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
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGen, Random random, BlockPos pos, StatueGenConfig config) {
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 3);
        BlockState state = RocketSquidsBase.BLOCK_STATUE.getDefaultState();
        world.setBlockState(pos, state, 3);
        RocketSquidsBase.BLOCK_STATUE.tick(state, (World) world, pos, random);
        return true;
    }
}
