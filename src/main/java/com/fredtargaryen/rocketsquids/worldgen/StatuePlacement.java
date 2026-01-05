package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.stream.Stream;

public class StatuePlacement extends PlacementModifier {
    public StatuePlacement(Codec<NoneFeatureConfiguration> codec) {
        super();
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(
            @NotNull PlacementContext helper,
            @NotNull Random random,
            @NotNull BlockPos pos
    ) {
        return Stream.of(pos);
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifierType.HEIGHT_RANGE;
    }
}
