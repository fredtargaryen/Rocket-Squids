// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.attachment.RocketSquidData;
import com.fredtargaryen.rocketsquids.level.block.ConchBlock;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static net.minecraft.world.level.block.Blocks.STONE;

public class RSAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    // Register all attachment types here
    public static final Supplier<AttachmentType<RocketSquidData>> SQUID = ATTACHMENT_TYPES.register("rocketsquid",
            () -> AttachmentType.serializable(() -> new RocketSquidData()).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
