package com.fredtargaryen.rocketsquids.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class StatueGenConfig implements FeatureConfiguration {

    public static final Codec<StatueGenConfig> FACTORY;

    public static final StatueGenConfig CONFIG = new StatueGenConfig();

    static {
        FACTORY = Codec.unit(() -> CONFIG);
    }

    public StatueGenConfig() {}
}
