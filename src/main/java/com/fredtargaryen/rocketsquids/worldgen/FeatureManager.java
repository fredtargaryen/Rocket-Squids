package com.fredtargaryen.rocketsquids.worldgen;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;

import static com.fredtargaryen.rocketsquids.RocketSquidsBase.*;

@Mod.EventBusSubscriber
public class FeatureManager {
    @SubscribeEvent
    public static void loadBiome(BiomeLoadingEvent ble)
    {
        MobSpawnSettingsBuilder builder = ble.getSpawns();
        List<MobSpawnSettings.SpawnerData> spawners = builder.getSpawner(MobCategory.WATER_CREATURE);
        boolean squidFound = false;
        for (MobSpawnSettings.SpawnerData s : spawners) {
            if(Objects.requireNonNull(s.type.getRegistryName()).toString().equals("minecraft:squid")) {
                squidFound = true;
            }
        }
        if(squidFound) builder.addSpawn(MobCategory.WATER_CREATURE, ROCKET_SQUID_SPAWN_INFO);
        BiomeGenerationSettingsBuilder bgsb = ble.getGeneration();
        bgsb.getFeatures(GenerationStep.Decoration.RAW_GENERATION).add(() -> STATUE_FEATURE.get().configured(new StatueGenConfig()).decorated(STATUE_PLACEMENT.get().configured(NoneFeatureConfiguration.INSTANCE)));
        if(ble.getCategory() == Biome.BiomeCategory.BEACH)
        {
            bgsb.getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONCH_FEATURE.get().configured(new ConchGenConfig()).decorated(CONCH_PLACEMENT.get().configured(NoneFeatureConfiguration.INSTANCE)));
        }
    }
}
