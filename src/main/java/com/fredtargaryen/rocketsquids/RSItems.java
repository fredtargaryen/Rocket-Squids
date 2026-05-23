// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Register all items here
    public static final DeferredItem<ConchItem> ITEM_CONCH = ITEMS.registerItem("conch_1", ConchItem::new, props -> props.stacksTo(4));
    @SuppressWarnings("unused")
    public static final DeferredItem<Conch2Item> ITEM_CONCH2 = ITEMS.registerItem("conch_2", Conch2Item::new, props -> props.stacksTo(1).rarity(Rarity.UNCOMMON));
    @SuppressWarnings("unused")
    public static final DeferredItem<Conch3Item> ITEM_CONCH3 = ITEMS.registerItem("conch_3", Conch3Item::new, props -> props.stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<NitroInkSacItem> NITRO_SAC = ITEMS.registerItem("nitro_ink_sac", NitroInkSacItem::new, props -> props.stacksTo(16));
    public static final DeferredItem<TurboTubeItem> TURBO_TUBE = ITEMS.registerItem("turbo_tube", TurboTubeItem::new, props -> props.stacksTo(16).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> ITEM_STATUE_CLOSED = ITEMS.registerSimpleBlockItem("statue_closed", RSBlocks.STATUE, props -> props.stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> ITEM_STATUE_OPEN = ITEMS.registerSimpleBlockItem("statue_open", RSBlocks.STATUE, props -> props.stacksTo(1));
    public static final DeferredItem<SqueleporterItem> SQUELEPORTER_ACTIVE = ITEMS.registerItem("squeleporter_active", SqueleporterItem::new, props -> props.stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<SqueleporterItem> SQUELEPORTER_INACTIVE = ITEMS.registerItem("squeleporter_inactive", SqueleporterItem::new, props -> props.stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<SpawnEggItem> ROCKET_SQUID_SPAWN_EGG = ITEMS.registerItem("rocket_squid_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(RSEntityTypes.SQUID_TYPE.get())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
