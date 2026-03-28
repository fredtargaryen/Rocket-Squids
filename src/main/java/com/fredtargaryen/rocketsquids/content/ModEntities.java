// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content;

import com.fredtargaryen.rocketsquids.content.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.content.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.content.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.content.item.RocketSquidForgeSpawnEggItem;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // Register all EntityTypes here
    public static final RegistryObject<EntityType<ThrownSacEntity>> SAC_TYPE = ENTITIES.register("thrown_nitro_ink_sac",
            () -> EntityType.Builder.<ThrownSacEntity>of(ThrownSacEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    public static final RegistryObject<EntityType<ThrownTubeEntity>> TUBE_TYPE = ENTITIES.register("turbo_tube",
            () -> EntityType.Builder.<ThrownTubeEntity>of(ThrownTubeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .setCustomClientFactory(ThrownTubeEntity::new)
                    .build(MODID)
    );
    public static final RegistryObject<EntityType<RocketSquidEntity>> SQUID_TYPE = ENTITIES.register("rocket_squid",
            () -> EntityType.Builder.<RocketSquidEntity>of((type, world) -> new RocketSquidEntity(world), MobCategory.WATER_CREATURE)
                    .sized(0.99F, 0.99F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    @SuppressWarnings("RedundantTypeArguments")
    public static final RegistryObject<EntityType<BabyRocketSquidEntity>> BABY_SQUID_TYPE = ENTITIES.register("baby_rocket_squid",
            () -> EntityType.Builder.<BabyRocketSquidEntity>of(BabyRocketSquidEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );

    private static final DeferredRegister<Item> SPAWNEGGITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Register Spawn Egg Items here
    @SuppressWarnings("unused")
    public static final RegistryObject<RocketSquidForgeSpawnEggItem> SQUID_SPAWN_EGG = SPAWNEGGITEMS.register("rockets_squid_spawn_egg",
            () -> new RocketSquidForgeSpawnEggItem(ModEntities.SQUID_TYPE, ModEntities.BABY_SQUID_TYPE, ColorHelper.getColor(150, 30, 30), ColorHelper.getColor(255, 127, 0), new Item.Properties())
    ); // Hey if you wanted to know do not use SpawnEggItem use ForgeSpawnEggItem

    public static void register(IEventBus eventBus) {
        eventBus.register(ModEntities.class);
        ENTITIES.register(eventBus);
        SPAWNEGGITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // Entity attributes are stored in there class under the createAttributes() method not in the registry code
        event.put(ModEntities.SQUID_TYPE.get(), RocketSquidEntity.createAttributes().build());
        event.put(ModEntities.BABY_SQUID_TYPE.get(), BabyRocketSquidEntity.createAttributes().build());
    }
}
