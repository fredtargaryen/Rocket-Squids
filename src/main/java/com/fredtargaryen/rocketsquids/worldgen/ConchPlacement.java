package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class ConchPlacement extends Placement<NoPlacementConfig> {
    public ConchPlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, NoPlacementConfig config, BlockPos pos) {
        int maxConches = random.nextInt(3); //was 3
        int blockX = (pos.getX() / 16) * 16;
        int blockZ = (pos.getZ() / 16) * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos groundPos = new BlockPos(conchX, helper.func_242893_a(Heightmap.Type.WORLD_SURFACE, conchX, conchZ) - 1, conchZ);
            BlockState blockState = helper.func_242894_a(groundPos);
            if (blockState.getMaterial() == Material.SAND) {
                return Stream.of(groundPos.up());
            }
        }
        return Stream.empty();
    }
}
