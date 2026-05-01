// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.levelgen.features;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.config.CommonConfig;
import com.fredtargaryen.rocketsquids.RSBlocks;
import com.fredtargaryen.rocketsquids.level.StatueData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class StatueGen extends Feature<NoneFeatureConfiguration> {

    public StatueGen() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Generate the feature at the given BlockPos (which was validated by an IPatchPlacement instance).
     *
     * @param context context
     * @return return
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        // First we create a few variables out of the context in order to adapt from the old way place was written
        WorldGenLevel level = context.level();
        ChunkGenerator chunkGen = context.chunkGenerator();
        RandomSource random = context.random();
        BlockPos pos = context.origin();
        // Then we check the config to see if this dimension is allowed
        if (CommonConfig.STATUE_USE_WHITELIST) {
            List<? extends String> allowedDimensions = CommonConfig.STATUE_WHITELIST;
            if (!allowedDimensions.contains(level.getLevel().dimension().location().toString())) return false;
        } else {
            List<? extends String> blockedDimensions = CommonConfig.STATUE_BLACKLIST;
            if (blockedDimensions.contains(level.getLevel().dimension().location().toString())) return false;
        }
        StatueData statueManager = StatueData.forLevel(level.getLevel());
        int frequency = CommonConfig.STATUE_FREQUENCY;
        int chunkX = StatueData.posToChunk(pos.getX());
        int chunkZ = StatueData.posToChunk(pos.getZ());
        int chunkAreaX = StatueData.posToChunkArea(pos.getX());
        int chunkAreaZ = StatueData.posToChunkArea(pos.getZ());
        int[] statueLocation = statueManager.getChunkArea(chunkAreaX, chunkAreaZ);
        if (statueLocation == null) {
            //A statue location hasn't been decided for this chunk area. Decide one
            statueLocation = new int[]{chunkAreaX, chunkAreaZ,
                    //Random chunk in the sizexsize area
                    (chunkAreaX * frequency + random.nextInt(frequency))
                            //Random block in the 16x16 chunk
                            * 16 + random.nextInt(16),
                    0,
                    (chunkAreaZ * frequency + random.nextInt(frequency))
                            * 16 + random.nextInt(16)};
            statueManager.addStatue(statueLocation);
        }
        if (chunkX == StatueData.posToChunk(statueLocation[2]) && chunkZ == StatueData.posToChunk(statueLocation[4])) {
            int chunkMinY = chunkGen.getMinY();
            int chunkMaxY = chunkMinY + chunkGen.getGenDepth() - 2;
            List<Integer> statueBasePositions = new ArrayList<>();
            BlockPos placePos = new BlockPos(statueLocation[2], chunkMinY, statueLocation[4]);
            statueManager.removeStatue(statueLocation);
            // Find all instances in this column of a solid block with a viable block above
            while (placePos.getY() < chunkMaxY) {
                if (level.getBlockState(placePos).isSolid() && isBlockStateValidForStatue(level.getBlockState(placePos.above()))) {
                    statueBasePositions.add(placePos.getY());
                }
                placePos = placePos.above();
            }

            if (!statueBasePositions.isEmpty()) {
                placePos = new BlockPos(
                        placePos.getX(),
                        statueBasePositions.get(level.getRandom().nextInt(statueBasePositions.size())) + 1,
                        placePos.getZ());
                Direction facing = DataReference.randomHorizontalFacing(level.getRandom());
                FluidState fs = level.getFluidState(placePos);
                level.setBlock(placePos, RSBlocks.STATUE.get().defaultBlockState()
                        .setValue(HORIZONTAL_FACING, facing)
                        .setValue(WATERLOGGED, fs.is(Fluids.WATER)), 3);
                fs = level.getFluidState(placePos.above());
                level.setBlock(placePos.above(), RSBlocks.STATUE.get().defaultBlockState()
                        .setValue(HORIZONTAL_FACING, facing)
                        .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
                        .setValue(WATERLOGGED, fs.is(Fluids.WATER)), 3);
                statueLocation[3] = placePos.getY();
                statueManager.addStatue(statueLocation);
                return true;
            }
        }
        return false;
    }

    private static boolean isBlockStateValidForStatue(BlockState state) {
        return state.isAir() || state.getBlock() == Blocks.WATER;
    }
}
