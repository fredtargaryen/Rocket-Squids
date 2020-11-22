package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class StatuePlacementConfig implements IPlacementConfig {
    public static final Codec<StatuePlacementConfig> FACTORY = null;

    public int genChance;

    public StatuePlacementConfig() { }
}
