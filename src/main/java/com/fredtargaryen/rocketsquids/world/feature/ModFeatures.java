package com.fredtargaryen.rocketsquids.world.feature;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.worldgen.features.ConchGen;
import com.fredtargaryen.rocketsquids.worldgen.features.ConchGenConfig;
import com.fredtargaryen.rocketsquids.worldgen.features.StatueGen;
import com.fredtargaryen.rocketsquids.worldgen.features.StatueGenConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, MODID);

    public static final RegistryObject<ConchGen> CONCH_FEATURE = FEATURES.register("conchgen",
            () -> new ConchGen(ConchGenConfig.FACTORY)
    );
    public static final RegistryObject<StatueGen> STATUE_FEATURE = FEATURES.register("statuegen",
            () -> new StatueGen(StatueGenConfig.FACTORY)
    );

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
