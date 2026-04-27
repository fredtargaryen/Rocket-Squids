// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.level.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.level.item.RocketSquidForgeSpawnEggItem;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    // Register all EntityTypes here
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownSacEntity>> SAC_TYPE = ENTITIES.register("thrown_nitro_ink_sac",
            () -> EntityType.Builder.<ThrownSacEntity>of(ThrownSacEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownTubeEntity>> TUBE_TYPE = ENTITIES.register("turbo_tube",
            () -> EntityType.Builder.<ThrownTubeEntity>of(ThrownTubeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    //.setCustomClientFactory(ThrownTubeEntity::new)
                    .build(MODID)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RocketSquidEntity>> SQUID_TYPE = ENTITIES.register("rocket_squid",
            () -> EntityType.Builder.<RocketSquidEntity>of((type, world) -> new RocketSquidEntity(world), MobCategory.WATER_CREATURE)
                    .sized(0.99F, 0.99F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    @SuppressWarnings("RedundantTypeArguments")
    public static final DeferredHolder<EntityType<?>, EntityType<BabyRocketSquidEntity>> BABY_SQUID_TYPE = ENTITIES.register("baby_rocket_squid",
            () -> EntityType.Builder.<BabyRocketSquidEntity>of(BabyRocketSquidEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );

    private static final DeferredRegister<Item> SPAWNEGGITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    // Register Spawn Egg Items here
    @SuppressWarnings("unused")
    public static final DeferredHolder<Item, RocketSquidForgeSpawnEggItem> SQUID_SPAWN_EGG = SPAWNEGGITEMS.register("rocket_squid_spawn_egg",
            () -> new RocketSquidForgeSpawnEggItem(RSEntityTypes.SQUID_TYPE, RSEntityTypes.BABY_SQUID_TYPE, ColorHelper.getColor(150, 30, 30), ColorHelper.getColor(255, 127, 0), new Item.Properties())
    ); // Hey if you wanted to know do not use SpawnEggItem use ForgeSpawnEggItem

    public static void register(IEventBus eventBus) {
        eventBus.register(RSEntityTypes.class);
        ENTITIES.register(eventBus);
        SPAWNEGGITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // Entity attributes are stored in their class under the createAttributes() method, not in the registry code
        event.put(RSEntityTypes.SQUID_TYPE.get(), RocketSquidEntity.createAttributes().build());
        event.put(RSEntityTypes.BABY_SQUID_TYPE.get(), BabyRocketSquidEntity.createAttributes().build());
    }
}
