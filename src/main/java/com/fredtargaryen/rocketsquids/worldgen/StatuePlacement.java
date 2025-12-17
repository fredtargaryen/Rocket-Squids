package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.stream.Stream;

public class StatuePlacement extends FeatureDecorator<NoneDecoratorConfiguration> {
    public StatuePlacement(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(
            @NotNull DecorationContext helper,
            @NotNull Random random,
            @NotNull NoneDecoratorConfiguration config,
            @NotNull BlockPos pos
    ) {
        return Stream.of(pos);
    }
}
