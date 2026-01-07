package com.fredtargaryen.rocketsquids.world.feature;

import com.fredtargaryen.rocketsquids.worldgen.placements.ConchPlacement;
import com.fredtargaryen.rocketsquids.worldgen.placements.StatuePlacement;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModPlacedFeatures {
    private static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);

    public static final RegistryObject<PlacedFeature> CONCH_PLACEMENT = PLACED_FEATURES.register("conchplace",
            () -> new PlacedFeature(ModConfiguredFeatures.CONCH_FEATURE.getHolder().get(), List.of(new ConchPlacement(NoneFeatureConfiguration.CODEC)))
    );
    public static final RegistryObject<PlacedFeature> STATUE_PLACEMENT = PLACED_FEATURES.register("statueplace",
            () -> new PlacedFeature(ModConfiguredFeatures.STATUE_FEATURE.getHolder().get(), List.of(new StatuePlacement(NoneFeatureConfiguration.CODEC)))
    );

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}
