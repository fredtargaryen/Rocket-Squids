package com.fredtargaryen.rocketsquids.world.feature;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.worldgen.features.ConchGenConfig;
import com.fredtargaryen.rocketsquids.worldgen.features.StatueGenConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONCH_CONFIG_FEATURE = CONFIGURED_FEATURES.register("conchgen",
            () -> new ConfiguredFeature<>(ModFeatures.CONCH_FEATURE.get(), new ConchGenConfig())
    );
    public static final RegistryObject<ConfiguredFeature<?, ?>> STATUE_CONFIG_FEATURE = CONFIGURED_FEATURES.register("statuegen",
            () -> new ConfiguredFeature<>(ModFeatures.STATUE_FEATURE.get(), new StatueGenConfig())
    );

    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
