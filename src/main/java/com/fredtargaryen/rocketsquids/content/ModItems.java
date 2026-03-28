// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content;

import com.fredtargaryen.rocketsquids.content.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Register all items here
    public static final RegistryObject<Item> ITEM_CONCH = ITEMS.register("conch_item_1", () -> new ItemConch(new Item.Properties().stacksTo(4)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH2 = ITEMS.register("conch_item_2", () -> new ItemConch2(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH3 = ITEMS.register("conch_item_3", () -> new ItemConch3(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NITRO_SAC = ITEMS.register("nitro_ink_sac", () -> new ItemNitroInkSac(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> TURBO_TUBE = ITEMS.register("turbo_tube", () -> new ItemTurboTube(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ITEM_STATUE = ITEMS.register("statue", () -> new BlockItem(ModBlocks.BLOCK_STATUE.get(), new Item.Properties().stacksTo(4).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SQUELEPORTER_ACTIVE = ITEMS.register("squeleporter_active", () -> new ItemSqueleporter(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SQUELEPORTER_INACTIVE = ITEMS.register("squeleporter_inactive", () -> new ItemSqueleporter(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
