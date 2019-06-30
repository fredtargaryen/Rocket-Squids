package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ConchGenConfig implements IFeatureConfig {
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
        return null;
    }

    public static ConchGenConfig factory(Dynamic data) {
        return new ConchGenConfig();
    }
}
