package com.fredtargaryen.rocketsquids.worldgen;

import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.BasePlacement;

import java.util.Random;

public class ConchPlacement extends BasePlacement<ConchPlacementConfig> {
    @Override
    public <C extends IFeatureConfig> boolean generate(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, ConchPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
        int maxConches = random.nextInt(3); //was 3
        int blockX = (pos.getX() / 16) * 16;
        int blockZ = (pos.getZ() / 16) * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos conchPos = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(conchX, 0, conchZ)).down();
            IBlockState blockState = world.getBlockState(conchPos);
            if (blockState.getBlock() instanceof BlockSand) {
                featureIn.func_212245_a(world, chunkGenerator, random, pos.up(), featureConfig);
            }
            else if(blockState.getMaterial() == Material.WATER) {
                BlockPos down = conchPos.down();
                if (world.getBlockState(down.down()).getMaterial() == Material.SAND) {
                    featureIn.func_212245_a(world, chunkGenerator, random, down, featureConfig);
                }
            }
        }
        return false;
    }
}
