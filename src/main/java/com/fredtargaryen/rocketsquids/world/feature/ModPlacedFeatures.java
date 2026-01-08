package com.fredtargaryen.rocketsquids.world.feature;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModPlacedFeatures {
    private static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);

    public static final RegistryObject<PlacedFeature> CONCH_PLACEMENT = PLACED_FEATURES.register("conchplace",
            () -> new PlacedFeature(ModConfiguredFeatures.CONCH_FEATURE.getHolder().get(), List.of(BiomeFilter.biome(), CountPlacement.of(3), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE)))
    );
    public static final RegistryObject<PlacedFeature> STATUE_PLACEMENT = PLACED_FEATURES.register("statueplace",
            () -> new PlacedFeature(ModConfiguredFeatures.STATUE_FEATURE.getHolder().get(), List.of(BlockPredicateFilter.forPredicate(BlockPredicate.alwaysTrue())))
    );

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}
