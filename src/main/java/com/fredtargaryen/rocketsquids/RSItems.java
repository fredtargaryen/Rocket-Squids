// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    // Register all items here
    public static final DeferredHolder<Item, ConchItem> ITEM_CONCH = ITEMS.register("conch_item_1", () -> new ConchItem(new Item.Properties().stacksTo(4)));
    @SuppressWarnings("unused")
    public static final DeferredHolder<Item, Conch2Item> ITEM_CONCH2 = ITEMS.register("conch_item_2", () -> new Conch2Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    @SuppressWarnings("unused")
    public static final DeferredHolder<Item, Conch3Item> ITEM_CONCH3 = ITEMS.register("conch_item_3", () -> new Conch3Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, NitroInkSacItem> NITRO_SAC = ITEMS.register("nitro_ink_sac", () -> new NitroInkSacItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, TurboTubeItem> TURBO_TUBE = ITEMS.register("turbo_tube", () -> new TurboTubeItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, BlockItem> ITEM_STATUE_CLOSED = ITEMS.register("statue_closed", () -> new BlockItem(RSBlocks.STATUE.get(), new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, BlockItem> ITEM_STATUE_OPEN = ITEMS.register("statue_open", () -> new BlockItem(RSBlocks.STATUE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, SqueleporterItem> SQUELEPORTER_ACTIVE = ITEMS.register("squeleporter_active", () -> new SqueleporterItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, SqueleporterItem> SQUELEPORTER_INACTIVE = ITEMS.register("squeleporter_inactive", () -> new SqueleporterItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
