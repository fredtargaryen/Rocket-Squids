// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@Mod(value = MODID)
public class RocketSquidsBase {
    // Get our logger
    public static final Logger LOGGER = LogManager.getLogger();

    public static MobSpawnSettings.SpawnerData ROCKET_SQUID_SPAWN_INFO;

    /**
     * A custom firework that looks kinda like a Rocket Squid, created in {@link RocketSquidsBase#setupFirework()}
     * Firework structure:
     * TagCompound          (firework)
     * |_TagList            (list, "Explosions")
     * |_TagCompound      (Single firework part)
     * |_TagBoolean     ("Trail")
     * |_TagBoolean     ("Flicker")
     * |_TagIntArray    ("Colors")
     * |_TagIntArray    ("FadeColors")
     */
    public static final CompoundTag firework = new CompoundTag();

    /**
     * Set up the tag describing the rocket squid firework
     */
    public static void setupFirework() {
        ListTag list = new ListTag();
        CompoundTag f1 = new CompoundTag();
        f1.putBoolean("Flicker", false);
        f1.putBoolean("Trail", false);
        f1.putIntArray("Colors", new int[]{15435844});
        f1.putIntArray("FadeColors", new int[]{6719955});
        list.add(f1);

        firework.put("Explosions", list);
    }

    public RocketSquidsBase(IEventBus eventBus, ModContainer modContainer) {
        RSBlocks.register(eventBus);
        RSItems.register(eventBus);
        RSArmorMaterials.register(eventBus);
        RSAttachmentTypes.register(eventBus);
        // Also populates the creative tab
        RSCreativeTabs.register(eventBus);
        RSDataComponentTypes.register(eventBus);
        // Also registers the spawn egg
        RSEntityTypes.register(eventBus);
        // Register our world gen features
        RSFeatures.register(eventBus);
        RSParticleTypes.register(eventBus);
        RSSounds.register(eventBus);

        //NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that NeoForge can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        IEventBus loadingBus = modContainer.getEventBus();
        loadingBus.addListener(this::postRegistration);
        loadingBus.addListener(this::registerSpawnPlacements);
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        setupFirework();
    }

    /**
     * Register conditions for spawning of this mod's entities
     */
    public void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(RSEntityTypes.SQUID_TYPE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }
}