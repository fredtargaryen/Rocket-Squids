package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.SandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConchPlacement extends Placement<ConchPlacementConfig> {
    public ConchPlacement(Function<Dynamic<?>, ? extends ConchPlacementConfig> func) {
        super(func);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, ConchPlacementConfig placementConfig, BlockPos pos) {
        int maxConches = random.nextInt(3); //was 3
        int blockX = (pos.getX() / 16) * 16;
        int blockZ = (pos.getZ() / 16) * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos conchPos = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(conchX, 0, conchZ)).down();
            BlockState blockState = world.getBlockState(conchPos);
            if (blockState.getBlock() instanceof SandBlock) {
                return Stream.of(conchPos);
            }
            else if(blockState.getMaterial() == Material.WATER) {
                BlockPos down = conchPos.down();
                if (world.getBlockState(down.down()).getMaterial() == Material.SAND) {
                    return Stream.of(conchPos);
                }
            }
        }
        return Stream.empty();
    }
}
