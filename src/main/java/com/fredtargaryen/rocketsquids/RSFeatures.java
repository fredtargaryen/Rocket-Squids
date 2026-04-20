// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.levelgen.features.ConchGen;
import com.fredtargaryen.rocketsquids.level.levelgen.features.StatueGen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, MODID);

    public static final DeferredHolder<Feature<?>, ConchGen> CONCH_FEATURE = FEATURES.register("conchgen",
            ConchGen::new
    );
    public static final DeferredHolder<Feature<?>, StatueGen> STATUE_FEATURE = FEATURES.register("statuegen",
            StatueGen::new
    );

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
