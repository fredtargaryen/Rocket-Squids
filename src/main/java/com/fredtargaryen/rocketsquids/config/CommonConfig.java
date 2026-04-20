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

@EventBusSubscriber(modid = DataReference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static List<String> DEFAULT_BLACKLIST = Collections.emptyList();
    public static List<String> DEFAULT_WHITELIST = Collections.singletonList("overworld");

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue _SPAWN_PROB = BUILDER
            .comment("Weighted probability of a group of Rocket Squids spawning.")
            .translation("config.common.squid.spawn.prob")
            .defineInRange("squid.spawn.prob", 3, 1, 100);

    private static final ModConfigSpec.IntValue _MIN_GROUP_SIZE = BUILDER
            .comment("Smallest possible size of a group.")
            .translation("config.common.squid.spawn.min")
            .defineInRange("squid.spawn.min", 1, 1, 20);

    private static final ModConfigSpec.IntValue _MAX_GROUP_SIZE = BUILDER
            .comment("Largest possible size of a group.")
            .translation("config.common.squid.spawn.max")
            .defineInRange("squid.spawn.max", 4, 1, 40);

    private static final ModConfigSpec.IntValue _BREED_COOLDOWN = BUILDER
            .comment("How much time in ticks do Rocket Squids have to wait before being able to breed again.")
            .translation("config.common.squid.breeding.cooldown")
            .defineInRange("squid.breeding.cooldown", 3600, 20, 72000);

    private static final ModConfigSpec.BooleanValue _ROCKET_SQUID_EXPLOSIONS_DESTROY = BUILDER
            .comment("If Rocket Squids that have been set on fire should destroy blocks when they explode.")
            .translation("config.common.squid.explosion.destructive")
            .define("squid.explosion.destructive", true);

    private static final ModConfigSpec.BooleanValue _CONCH_USE_WHITELIST = BUILDER
            .comment("If true, uses the conch whitelist to determine where conches can be found. If false, uses the blacklist.")
            .translation("config.common.worldgen.conch.usewhitelist")
            .define("worldgen.conch.usewhitelist", true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> _CONCH_WHITELIST = BUILDER
            .comment("The list of dimensions where conches can appear.")
            .translation("config.common.worldgen.conch.whitelist")
            .defineList("worldgen.conch.whitelist", DEFAULT_WHITELIST, string -> true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> _CONCH_BLACKLIST = BUILDER
            .comment("The list of dimensions where conches cannot appear.")
            .translation("config.common.worldgen.conch.blacklist")
            .defineList("worldgen.conch.blacklist", DEFAULT_BLACKLIST, string -> true);

    private static final ModConfigSpec.BooleanValue _STATUE_USE_WHITELIST = BUILDER
            .comment("If true, uses the statue whitelist to determine where statues can be found. If false, uses the blacklist.")
            .translation("config.common.worldgen.statue.usewhitelist")
            .define("worldgen.statue.usewhitelist", true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> _STATUE_WHITELIST = BUILDER
            .comment("The list of dimensions where statues can appear.")
            .translation("config.common.worldgen.statue.whitelist")
            .defineList("worldgen.statue.whitelist", DEFAULT_WHITELIST, string -> true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> _STATUE_BLACKLIST = BUILDER
            .comment("The list of dimensions where statues cannot appear.")
            .translation("config.common.worldgen.statue.blacklist")
            .defineList("worldgen.statue.blacklist", DEFAULT_BLACKLIST, string -> true);

    private static final ModConfigSpec.IntValue _STATUE_FREQUENCY = BUILDER
            .comment("One statue will appear in every nxn chunk area. Changing this in an existing world is not recommended.")
            .translation("config.common.worldgen.statue.frequency")
            .defineInRange("worldgen.statue.frequency", 4, 1, 2000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean CONCH_USE_WHITELIST;
    public static List<? extends String> CONCH_WHITELIST;
    public static List<? extends String> CONCH_BLACKLIST;

    public static int SPAWN_PROB;
    public static int MIN_GROUP_SIZE;
    public static int MAX_GROUP_SIZE;
    public static int BREED_COOLDOWN;
    public static boolean ROCKET_SQUID_EXPLOSIONS_DESTROY;

    public static boolean STATUE_USE_WHITELIST;
    public static List<? extends String> STATUE_WHITELIST;
    public static List<? extends String> STATUE_BLACKLIST;
    public static int STATUE_FREQUENCY;

    @SubscribeEvent
    static void commonSetup(final FMLCommonSetupEvent event) {
        CONCH_USE_WHITELIST = _CONCH_USE_WHITELIST.get();
        CONCH_WHITELIST = _CONCH_WHITELIST.get();
        CONCH_BLACKLIST = _CONCH_BLACKLIST.get();

        SPAWN_PROB = _SPAWN_PROB.get();
        MIN_GROUP_SIZE = _MIN_GROUP_SIZE.get();
        MAX_GROUP_SIZE = _MAX_GROUP_SIZE.get();
        BREED_COOLDOWN = _BREED_COOLDOWN.get();
        ROCKET_SQUID_EXPLOSIONS_DESTROY = _ROCKET_SQUID_EXPLOSIONS_DESTROY.get();

        STATUE_USE_WHITELIST = _STATUE_USE_WHITELIST.get();
        STATUE_WHITELIST = _STATUE_WHITELIST.get();
        STATUE_BLACKLIST = _STATUE_BLACKLIST.get();
        STATUE_FREQUENCY = _STATUE_FREQUENCY.get();
    }
}
