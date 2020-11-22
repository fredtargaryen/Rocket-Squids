package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

import static com.fredtargaryen.rocketsquids.RocketSquidsBase.*;

@Mod.EventBusSubscriber
public class FeatureManager {
    public static ConchPlacement CONCH_PLACEMENT;
    public static StatuePlacement STATUE_PLACEMENT;

    public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        CONCH_FEATURE = new ConchGen(ConchGenConfig.FACTORY);
        CONCH_FEATURE.setRegistryName("conchgen");
        CONCH_PLACEMENT = new ConchPlacement(NoPlacementConfig.CODEC);
        STATUE_FEATURE = new StatueGen(StatueGenConfig.FACTORY);
        STATUE_FEATURE.setRegistryName("statuegen");
        STATUE_PLACEMENT = new StatuePlacement(NoPlacementConfig.CODEC);
        event.getRegistry().registerAll(CONCH_FEATURE, STATUE_FEATURE);
    }

    @SubscribeEvent
    public static void loadBiome(BiomeLoadingEvent ble)
    {
        Set<EntityType<?>> spawns = ble.getSpawns().getEntityTypes();
        if(spawns.contains(EntityType.SQUID))
        {
            List<MobSpawnInfo.Spawners> spawners = ble.getSpawns().getSpawner(EntityClassification.WATER_CREATURE);
            for (MobSpawnInfo.Spawners s : spawners) {
                if(s.type == RocketSquidsBase.SQUID_TYPE) {
                    ble.getSpawns().withSpawner(EntityClassification.WATER_CREATURE, ROCKET_SQUID_SPAWN_INFO);
                }
            }
            spawns.add(RocketSquidsBase.SQUID_TYPE);
        }
        BiomeGenerationSettingsBuilder bgsb = ble.getGeneration();
        bgsb.getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(() -> STATUE_FEATURE.withConfiguration(new StatueGenConfig()).withPlacement(STATUE_PLACEMENT.configure(NoPlacementConfig.INSTANCE)));
        if(ble.getCategory() == Biome.Category.BEACH)
        {
            bgsb.getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONCH_FEATURE.withConfiguration(new ConchGenConfig()).withPlacement(CONCH_PLACEMENT.configure(NoPlacementConfig.INSTANCE)));
        }
    }
}
