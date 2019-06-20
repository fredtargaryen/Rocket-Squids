package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class StatueGenConfig implements IFeatureConfig {
    @Override
    public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
        return null;
    }

    public static StatueGenConfig factory(Dynamic data) {
        return new StatueGenConfig();
    }
}
