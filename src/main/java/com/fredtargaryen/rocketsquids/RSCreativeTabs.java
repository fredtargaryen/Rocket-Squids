// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> ROCKET_SQUIDS_TAB = TABS.register("rocket_squid_tab", () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.translatable("item_group." + MODID + ".rocket_squid_tab"))
            // Set icon of creative tab
            .icon(() -> new ItemStack(RSItems.ITEM_CONCH.get()))
            // Add default items to tab
            .displayItems((params, output) -> {
                output.accept(RSItems.ITEM_CONCH.get());
                output.accept(RSItems.ITEM_CONCH2.get());
                output.accept(RSItems.ITEM_CONCH3.get());
                output.accept(RSItems.NITRO_SAC.get());
                output.accept(RSItems.TURBO_TUBE.get());
                output.accept(RSItems.ITEM_STATUE_CLOSED.get());
                output.accept(RSItems.ITEM_STATUE_OPEN.get());
                output.accept(RSItems.SQUELEPORTER_INACTIVE.get());
                output.accept(RSEntities.SQUID_SPAWN_EGG.get());
            })
            .build()
    );

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
