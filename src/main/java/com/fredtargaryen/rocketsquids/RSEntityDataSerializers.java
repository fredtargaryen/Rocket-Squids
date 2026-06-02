// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSEntityDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MODID);

    // Register all entity data serializers here
    public static final Supplier<EntityDataSerializer<Double>> DOUBLE = ENTITY_DATA_SERIALIZERS.register("double",
            () -> EntityDataSerializer.forValueType(ByteBufCodecs.DOUBLE)
    );

    public static void register(IEventBus eventBus) {
        ENTITY_DATA_SERIALIZERS.register(eventBus);
    }
}
