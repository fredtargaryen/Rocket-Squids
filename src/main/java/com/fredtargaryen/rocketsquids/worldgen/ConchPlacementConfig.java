package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ConchPlacementConfig implements FeatureConfiguration {
    public static final Codec<ConchPlacementConfig> FACTORY = null;

    public int genChance;

    public ConchPlacementConfig() { }
}
