package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.stream.Stream;

public class ConchPlacement extends PlacementModifier {
    public ConchPlacement(Codec<NoneFeatureConfiguration> codec) {
        super();
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(
            @NotNull PlacementContext helper,
            Random random,
            BlockPos pos
    ) {
        int maxConches = random.nextInt(3); //was 3
        int blockX = (pos.getX() / 16) * 16;
        int blockZ = (pos.getZ() / 16) * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos groundPos = new BlockPos(conchX, helper.getHeight(Heightmap.Types.WORLD_SURFACE, conchX, conchZ) - 1, conchZ);
            BlockState blockState = helper.getBlockState(groundPos);
            if (blockState.getMaterial() == Material.SAND) {
                return Stream.of(groundPos.above());
            }
        }
        return Stream.empty();
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifierType.HEIGHTMAP;
    }
}
