// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, MODID);

    // Register all attachment types here
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CONCH = ARMOR_MATERIALS.register("conch",
            () -> new ArmorMaterial(
                    new HashMap<>(),
                    0,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> null,
                    new ArrayList<>(),
                    0,
                    0
            )
    );

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
