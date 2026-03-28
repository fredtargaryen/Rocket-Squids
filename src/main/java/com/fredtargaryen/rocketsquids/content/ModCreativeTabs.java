// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> ROCKET_SQUIDS_TAB = TABS.register("rocket_squid_tab", () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.translatable("item_group." + MODID + ".rocket_squid_tab"))
            // Set icon of creative tab
            .icon(() -> new ItemStack(ModItems.ITEM_CONCH.get()))
            // Add default items to tab
            .displayItems((params, output) -> {
                output.accept(ModItems.ITEM_CONCH.get());
                output.accept(ModItems.ITEM_CONCH2.get());
                output.accept(ModItems.ITEM_CONCH3.get());
                output.accept(ModItems.NITRO_SAC.get());
                output.accept(ModItems.TURBO_TUBE.get());
                output.accept(ModItems.ITEM_STATUE.get());
                output.accept(ModItems.SQUAVIGATOR.get());
                output.accept(ModItems.SQUELEPORTER_INACTIVE.get());
                output.accept(ModEntities.SQUID_SPAWN_EGG.get());
            })
            .build()
    );

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
