package com.fredtargaryen.rocketsquids.worldgen;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Iterator;

public class FeatureManager {
    public void registerGenerators() {
        //Create the feature, its config, the placement controller and its config
        ConchGenConfig cgc = new ConchGenConfig();
        ConchGen cg = new ConchGen(ConchGenConfig::factory);
        ConchPlacementConfig cpc = new ConchPlacementConfig();
        ConchPlacement cp = new ConchPlacement(ConchPlacementConfig::factory);
        //Register these in all Biomes necessary
        Iterator<Biome> i = Biome.BIOMES.iterator();
        while(i.hasNext()) {
            Biome b = i.next();
            b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Biome.createDecoratedFeature(cg, cgc, cp, cpc));
        }
        
        //Create the feature, its config, the placement controller and its config
        StatueGenConfig sgc = new StatueGenConfig();
        StatueGen sg = new StatueGen(StatueGenConfig::factory);
        StatuePlacementConfig spc = new StatuePlacementConfig();
        StatuePlacement sp = new StatuePlacement(StatuePlacementConfig::factory);
        //Register these in all Biomes necessary
        Iterator<Biome> i2 = Biome.BIOMES.iterator();
        while(i2.hasNext()) {
            Biome b = i2.next();
            b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(sg, sgc, sp, spc));
        }
    }
}
