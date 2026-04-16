// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.levelgen.features.ConchGen;
import com.fredtargaryen.rocketsquids.level.levelgen.features.StatueGen;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static final RegistryObject<ConchGen> CONCH_FEATURE = FEATURES.register("conchgen",
            ConchGen::new
    );
    public static final RegistryObject<StatueGen> STATUE_FEATURE = FEATURES.register("statuegen",
            StatueGen::new
    );

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
