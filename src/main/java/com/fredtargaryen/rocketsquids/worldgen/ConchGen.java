package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
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
        // First check the config to see if this dimension is allowed
        if(GeneralConfig.CONCH_USE_WHITELIST.get())
        {
            List<String> allowedDimensions = GeneralConfig.CONCH_WHITELIST.get();
            if(!allowedDimensions.contains(world.getWorld().getDimensionKey().getLocation().toString())) return false;
        }
        else
        {
            List<String> blockedDimensions = GeneralConfig.CONCH_BLACKLIST.get();
            if(blockedDimensions.contains(world.getWorld().getDimensionKey().getLocation().toString())) return false;
        }
        world.setBlockState(pos, RocketSquidsBase.BLOCK_CONCH.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, DataReference.randomHorizontalFacing(random))
                .with(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() == Blocks.WATER), 3);
        return true;
    }
}
