// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.config;

import com.fredtargaryen.rocketsquids.DataReference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = DataReference.MODID)
public class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue _BREED_COOLDOWN = BUILDER
            .comment("How much time in ticks do Rocket Squids have to wait before being able to breed again.")
            .translation("config.common.squid.breeding.cooldown")
            .defineInRange("squid.breeding.cooldown", 3600, 20, 72000);

    private static final ModConfigSpec.BooleanValue _ROCKET_SQUID_EXPLOSIONS_DESTROY = BUILDER
            .comment("If Rocket Squids that have been set on fire should destroy blocks when they explode.")
            .translation("config.common.squid.explosion.destructive")
            .define("squid.explosion.destructive", true);

    private static final ModConfigSpec.IntValue _STATUE_FREQUENCY = BUILDER
            .comment("One statue will appear in every nxn chunk area. Changing this in an existing world is not recommended.")
            .translation("config.common.worldgen.statue.frequency")
            .defineInRange("worldgen.statue.frequency", 4, 1, 2000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int BREED_COOLDOWN;
    public static boolean ROCKET_SQUID_EXPLOSIONS_DESTROY;
    public static int STATUE_FREQUENCY;

    @SubscribeEvent
    static void commonSetup(final FMLCommonSetupEvent event) {
        BREED_COOLDOWN = _BREED_COOLDOWN.get();
        ROCKET_SQUID_EXPLOSIONS_DESTROY = _ROCKET_SQUID_EXPLOSIONS_DESTROY.get();
        STATUE_FREQUENCY = _STATUE_FREQUENCY.get();
    }
}
