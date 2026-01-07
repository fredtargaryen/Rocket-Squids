package com.fredtargaryen.rocketsquids.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ConchGenConfig implements FeatureConfiguration {

    public static final Codec<ConchGenConfig> FACTORY;

    public static final ConchGenConfig CONFIG = new ConchGenConfig();

    static {
        FACTORY = Codec.unit(() -> CONFIG);
    }

    public ConchGenConfig() {}
}
