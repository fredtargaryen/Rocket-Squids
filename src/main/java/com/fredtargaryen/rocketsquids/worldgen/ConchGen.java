package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.function.Function;

public class ConchGen extends Feature<ConchGenConfig> {

    public ConchGen(Function<Dynamic<?>, ? extends ConchGenConfig> p_i49878_1_) {
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
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGen, Random random, BlockPos pos, ConchGenConfig config) {
        world.setBlockState(pos, RocketSquidsBase.BLOCK_CONCH.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, DataReference.randomHorizontalFacing(random))
                .with(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() == Blocks.WATER), 3);
        return true;
    }
}
