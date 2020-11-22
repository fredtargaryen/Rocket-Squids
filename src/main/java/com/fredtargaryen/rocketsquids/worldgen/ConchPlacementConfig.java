package com.fredtargaryen.rocketsquids.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class ConchPlacementConfig implements IPlacementConfig {
    public static final Codec<ConchPlacementConfig> FACTORY = null;

    public int genChance;

    public ConchPlacementConfig() { }
}
