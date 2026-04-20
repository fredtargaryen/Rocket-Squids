// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSDataComponentTypes {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    // Register all data component types here
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SqueleporterData>> SQUELEPORTER = DATA_COMPONENT_TYPES.register("squeleporter",
            () -> DataComponentType.<SqueleporterData>builder()
                    .persistent(SqueleporterData.DISK_CODEC)
                    .networkSynchronized(SqueleporterData.NETWORK_CODEC)
                    .build()
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
