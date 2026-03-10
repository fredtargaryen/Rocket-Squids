package com.fredtargaryen.rocketsquids.content.worldgen.features;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConchGen extends Feature<NoneFeatureConfiguration> {
    public ConchGen() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     * @param context context
     * @return return
     */
    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        // First we create a few variables out of the context in order to adapt from the older way place() was written
        WorldGenLevel world = context.level();
        RandomSource random = context.random();
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

        // Check if the block below the conch has the ICE BlockTag and if it does then we don't place one there
        if (world.getBlockState(pos.below()).is(BlockTags.ICE)) {
            return false;
        }

        // Setting values
        world.setBlock(pos, ModBlocks.BLOCK_CONCH.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, DataReference.randomHorizontalFacing(random))
                .setValue(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() == Blocks.WATER), 3);
        return true;
    }
}
