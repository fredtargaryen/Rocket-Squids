package com.fredtargaryen.rocketsquids.worldgen;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;

import java.util.Iterator;

public class FeatureManager {
    public void registerGenerators() {
        //Create the feature, its config, the placement controller and its config
        ConchGenConfig cgc = new ConchGenConfig();
        ConchPlacementConfig cpc = new ConchPlacementConfig();
        ConchGen cg = new ConchGen();
        ConchPlacement cp = new ConchPlacement();
        //Register these in all Biomes necessary
        Iterator<Biome> i = Biome.BIOMES.iterator();
        while(i.hasNext()) {
            Biome b = i.next();
            b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Biome.createCompositeFeature(cg, cgc, cp, cpc));
        }
        
        //Create the feature, its config, the placement controller and its config
        StatueGenConfig sgc = new StatueGenConfig();
        StatuePlacementConfig spc = new StatuePlacementConfig();
        StatueGen sg = new StatueGen();
        StatuePlacement sp = new StatuePlacement();
        //Register these in all Biomes necessary
        Iterator<Biome> i2 = Biome.BIOMES.iterator();
        while(i2.hasNext()) {
            Biome b = i2.next();
            b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createCompositeFeature(sg, sgc, sp, spc));
        }
    }
}
