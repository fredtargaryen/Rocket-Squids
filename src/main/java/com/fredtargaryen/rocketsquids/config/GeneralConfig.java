// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class GeneralConfig {
    public static ForgeConfigSpec.IntValue SPAWN_PROB;

    public static ForgeConfigSpec.IntValue MIN_GROUP_SIZE;

    public static ForgeConfigSpec.IntValue MAX_GROUP_SIZE;

    public static ForgeConfigSpec.BooleanValue CONCH_USE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CONCH_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CONCH_BLACKLIST;

    public static ForgeConfigSpec.BooleanValue STATUE_USE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> STATUE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> STATUE_BLACKLIST;

    public static final List<String> DEFAULT_WHITELIST = new ArrayList<>();

    public static final List<String> DEFAULT_BLACKLIST = new ArrayList<>();

    public static ForgeConfigSpec.IntValue STATUE_FREQUENCY;

    public static ForgeConfigSpec.IntValue BREED_COOLDOWN;

    public static ForgeConfigSpec.BooleanValue ROCKET_SQUID_EXPLOSIONS_DESTROY;

    public static void init(ForgeConfigSpec.Builder serverBuilder) {
        DEFAULT_WHITELIST.add("minecraft:overworld");
        SPAWN_PROB = serverBuilder.comment("Weighted probability of a group of Rocket Squids spawning.")
                .defineInRange("spawn.prob", 3, 1, 100);
        MIN_GROUP_SIZE = serverBuilder.comment("Smallest possible size of a group.")
                .defineInRange("spawn.min", 1, 1, 20);
        MAX_GROUP_SIZE = serverBuilder.comment("Largest possible size of a group.")
                .defineInRange("spawn.max", 4, 1, 40);
        CONCH_USE_WHITELIST = serverBuilder.comment("If true, uses the conch whitelist. If false, uses the blacklist.")
                .define("worldgen.conch.usewhitelist", true);
        CONCH_WHITELIST = serverBuilder.comment("The list of dimensions where conches can appear.")
                .defineList("worldgen.conch.whitelist", DEFAULT_WHITELIST, string -> true);
        CONCH_BLACKLIST = serverBuilder.comment("The list of dimensions where conches cannot appear.")
                .defineList("worldgen.conch.blacklist", DEFAULT_BLACKLIST, string -> true);
        STATUE_USE_WHITELIST = serverBuilder.comment("If true, uses the statue whitelist. If false, uses the blacklist.")
                .define("worldgen.statue.usewhitelist", true);
        STATUE_WHITELIST = serverBuilder.comment("The list of dimensions where statues can appear.")
                .defineList("worldgen.statue.whitelist", DEFAULT_WHITELIST, string -> true);
        STATUE_BLACKLIST = serverBuilder.comment("The list of dimensions where statues cannot appear.")
                .defineList("worldgen.statue.blacklist", DEFAULT_BLACKLIST, string -> true);
        STATUE_FREQUENCY = serverBuilder.comment("One statue will appear in every nxn chunk area. Changing this in an existing world is not recommended.")
                .defineInRange("worldgen.statue.frequency", 4, 1, 2000);
        BREED_COOLDOWN = serverBuilder.comment("How much time in ticks do Rocket Squids have to wait before being able to breed again.")
                .defineInRange("entity.breeding.cooldown", 3600, 20, 72000);
        ROCKET_SQUID_EXPLOSIONS_DESTROY = serverBuilder.comment("If Rocket Squids that have been set on fire should destroy blocks when they explode.")
                .define("entity.explosion.destructive", true);
    }
}
