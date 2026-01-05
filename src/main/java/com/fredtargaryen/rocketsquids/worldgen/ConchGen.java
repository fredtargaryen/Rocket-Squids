package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ConchGen extends Feature<ConchGenConfig> {

    public ConchGen(Codec<ConchGenConfig> codec) {
        super(codec);
    }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     * @param context context
     * @return return
     */
    @Override
    public boolean place(@NotNull FeaturePlaceContext<ConchGenConfig> context) {
        // First we create a few variables out of the context in order to adapt from the old way place was written
        WorldGenLevel world = context.level();
        Random random = context.random();
        BlockPos pos = context.origin();
        // Then we check the config to see if this dimension is allowed
        if(GeneralConfig.CONCH_USE_WHITELIST.get())
        {
            List<? extends String> allowedDimensions = GeneralConfig.CONCH_WHITELIST.get();
            if(!allowedDimensions.contains(world.getLevel().dimension().location().toString())) return false;
        }
        else
        {
            List<? extends String> blockedDimensions = GeneralConfig.CONCH_BLACKLIST.get();
            if(blockedDimensions.contains(world.getLevel().dimension().location().toString())) return false;
        }
        world.setBlock(pos, RocketSquidsBase.BLOCK_CONCH.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, DataReference.randomHorizontalFacing(random))
                .setValue(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() == Blocks.WATER), 3);
        return true;
    }
}
