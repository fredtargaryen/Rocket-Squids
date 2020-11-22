package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class ConchGen extends Feature<ConchGenConfig> {

    public ConchGen(Codec<ConchGenConfig> codec) {
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
    public boolean generate(ISeedReader world, ChunkGenerator chunkGen, Random random, BlockPos pos, ConchGenConfig config) {
        world.setBlockState(pos, RocketSquidsBase.BLOCK_CONCH.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, DataReference.randomHorizontalFacing(random))
                .with(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() == Blocks.WATER), 3);
        return true;
    }
}
