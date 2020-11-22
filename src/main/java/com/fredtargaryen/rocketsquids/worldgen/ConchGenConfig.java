package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ConchGenConfig implements IFeatureConfig {

    public static final Codec<ConchGenConfig> FACTORY;

    public static final ConchGenConfig CONFIG = new ConchGenConfig();

    static {
        FACTORY = Codec.unit(() -> CONFIG);
    }

    public ConchGenConfig() {}
}
