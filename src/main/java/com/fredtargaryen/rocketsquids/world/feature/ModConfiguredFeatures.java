package com.fredtargaryen.rocketsquids.world.feature;

import com.fredtargaryen.rocketsquids.worldgen.features.ConchGen;
import com.fredtargaryen.rocketsquids.worldgen.features.ConchGenConfig;
import com.fredtargaryen.rocketsquids.worldgen.features.StatueGen;
import com.fredtargaryen.rocketsquids.worldgen.features.StatueGenConfig;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONCH_FEATURE = CONFIGURED_FEATURES.register("conchgen",
            () -> new ConfiguredFeature<>(new ConchGen(ConchGenConfig.FACTORY), new ConchGenConfig())
    );
    public static final RegistryObject<ConfiguredFeature<?, ?>> STATUE_FEATURE = CONFIGURED_FEATURES.register("statuegen",
            () -> new ConfiguredFeature<>(new StatueGen(StatueGenConfig.FACTORY), new StatueGenConfig())
    );

    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
