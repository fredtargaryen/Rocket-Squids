package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.world.feature.ModPlacedFeatures;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static com.fredtargaryen.rocketsquids.RocketSquidsBase.*;

@Mod.EventBusSubscriber(modid = MODID)
public class FeatureManager {
    @SubscribeEvent
    public static void loadBiome(BiomeLoadingEvent event)
    {
        // this code is for handling the spawning of rocket squid entities
        MobSpawnSettingsBuilder builder = event.getSpawns();
        List<MobSpawnSettings.SpawnerData> spawners = builder.getSpawner(MobCategory.WATER_CREATURE);
        boolean squidFound = false;
        for (MobSpawnSettings.SpawnerData s : spawners) {
            // this code checks if squids have spawned, if they have we set squidFound to true
            if(Objects.requireNonNull(s.type.getRegistryName()).toString().equals("minecraft:squid")) {
                squidFound = true;
            }
        }
        // then if we found squids then it means we have a valid spawn for rocket squids
        if(squidFound) {
            // so it spawns them
            builder.addSpawn(MobCategory.WATER_CREATURE, ROCKET_SQUID_SPAWN_INFO);
        }

        // this code is for handling the generation of Conch blocks and Statues
        BiomeGenerationSettingsBuilder bgsb = event.getGeneration();
        // using RAW_GENERATION as our decorator we add the placement through .getHolder.get()
        if (ModPlacedFeatures.STATUE_PLACEMENT.getHolder().isPresent()) {
            bgsb.addFeature(
                    GenerationStep.Decoration.RAW_GENERATION,
                    ModPlacedFeatures.STATUE_PLACEMENT.getHolder().get()
            );
        } else {
            rocketSquidLogger.error("Failed to find STATUE_PLACEMENT holder");
        }
        if(event.getCategory() == Biome.BiomeCategory.BEACH) // filters non-beach biomes out
        {
            // using TOP_LAYER_MODIFICATION as our decorator we add the placement through .getHolder.get()
            if (ModPlacedFeatures.CONCH_PLACEMENT.getHolder().isPresent()) {
                bgsb.addFeature(
                        GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                        ModPlacedFeatures.CONCH_PLACEMENT.getHolder().get()
                );
            } else {
                rocketSquidLogger.error("Failed to find CONCH_PLACEMENT holder");
            }
        }
    }
}
