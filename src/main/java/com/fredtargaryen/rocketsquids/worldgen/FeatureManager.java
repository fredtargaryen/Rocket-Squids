package com.fredtargaryen.rocketsquids.worldgen;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static com.fredtargaryen.rocketsquids.RocketSquidsBase.CONCH_FEATURE;
import static com.fredtargaryen.rocketsquids.RocketSquidsBase.STATUE_FEATURE;

public class FeatureManager {
    public static ConchPlacement CONCH_PLACEMENT;
    public static StatuePlacement STATUE_PLACEMENT;

    public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        CONCH_FEATURE = new ConchGen(ConchGenConfig::factory);
        CONCH_FEATURE.setRegistryName("conchgen");
        CONCH_PLACEMENT = new ConchPlacement(ConchPlacementConfig::factory);
        STATUE_FEATURE = new StatueGen(StatueGenConfig::factory);
        STATUE_FEATURE.setRegistryName("statuegen");
        STATUE_PLACEMENT = new StatuePlacement(StatuePlacementConfig::factory);
        event.getRegistry().registerAll(CONCH_FEATURE, STATUE_FEATURE);
    }

    public void registerGenerators() {
        //Create the feature, its config, the placement controller and its config
        ConchGenConfig cgc = new ConchGenConfig();
        ConchPlacementConfig cpc = new ConchPlacementConfig();
        //Register these in all Biomes necessary
        for(Biome b : ForgeRegistries.BIOMES) {
            b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, CONCH_FEATURE.withConfiguration(cgc).withPlacement(CONCH_PLACEMENT.configure(cpc)));
        }
        //Create the feature, its config, the placement controller and its config
        StatueGenConfig sgc = new StatueGenConfig();
        StatuePlacementConfig spc = new StatuePlacementConfig();
        //Register these in all Biomes necessary
        for(Biome b : ForgeRegistries.BIOMES){
            b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, STATUE_FEATURE.withConfiguration(sgc).withPlacement(STATUE_PLACEMENT.configure(spc)));
        }
    }
}
