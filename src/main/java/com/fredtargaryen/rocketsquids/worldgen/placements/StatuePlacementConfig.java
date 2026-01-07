package com.fredtargaryen.rocketsquids.worldgen.placements;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class StatuePlacementConfig implements FeatureConfiguration {
    public static final Codec<StatuePlacementConfig> FACTORY = null;

    public int genChance;

    public StatuePlacementConfig() { }
}
