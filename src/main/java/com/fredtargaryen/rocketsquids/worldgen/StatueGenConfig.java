package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class StatueGenConfig implements IFeatureConfig {

    public static final Codec<StatueGenConfig> FACTORY;

    public static final StatueGenConfig CONFIG = new StatueGenConfig();

    static {
        FACTORY = Codec.unit(() -> CONFIG);
    }

    public StatueGenConfig() {}
}
