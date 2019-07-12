package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;

import java.util.Iterator;

public class FeatureManager {
    public void registerGenerators() {
        //Create the feature, its config, the placement controller and its config
        ConchGenConfig cgc = new ConchGenConfig();
        ConchPlacementConfig cpc = new ConchPlacementConfig();
        //Register these in all Biomes necessary
        Iterator<Biome> i = Biome.BIOMES.iterator();
        while(i.hasNext()) {
            Biome b = i.next();
            b.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                    Biome.createDecoratedFeature(RocketSquidsBase.CONCH_GEN, cgc, RocketSquidsBase.CONCH_PLACE, cpc));
        }
        
        //Create the feature, its config, the placement controller and its config
        StatueGenConfig sgc = new StatueGenConfig();
        StatuePlacementConfig spc = new StatuePlacementConfig();
        //Register these in all Biomes necessary
        Iterator<Biome> i2 = Biome.BIOMES.iterator();
        while(i2.hasNext()) {
            Biome b = i2.next();
            b.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
                    Biome.createDecoratedFeature(RocketSquidsBase.STATUE_GEN, sgc, RocketSquidsBase.STATUE_PLACE, spc));
        }
    }
}
