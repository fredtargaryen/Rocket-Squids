package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class StatuePlacementConfig implements IPlacementConfig {
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
        return null;
    }

    public static StatuePlacementConfig factory(Dynamic data) {
        return new StatuePlacementConfig();
    }
}