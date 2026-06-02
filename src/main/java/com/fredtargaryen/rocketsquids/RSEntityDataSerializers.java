// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSEntityDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MODID);

    public static final RegistryObject<EntityDataSerializer<Double>> DOUBLE = ENTITY_DATA_SERIALIZERS.register(
            "double",
            () -> EntityDataSerializer.simple(RSEntityDataSerializers::writeDouble, RSEntityDataSerializers::readDouble)
    );

    public static void writeDouble(FriendlyByteBuf buf, Double d) {
        buf.writeDouble(d);
    }

    public static double readDouble(FriendlyByteBuf buf) {
        return buf.readDouble();
    }

    public static void register(IEventBus eventBus) {
        ENTITY_DATA_SERIALIZERS.register(eventBus);
    }
}
