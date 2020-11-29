package com.fredtargaryen.rocketsquids.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class GeneralConfig {
    public static ForgeConfigSpec.IntValue SPAWN_PROB;

    public static ForgeConfigSpec.IntValue MIN_GROUP_SIZE;

    public static ForgeConfigSpec.IntValue MAX_GROUP_SIZE;

    public static ForgeConfigSpec.BooleanValue CONCH_USE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<String>> CONCH_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<String>> CONCH_BLACKLIST;

    public static ForgeConfigSpec.BooleanValue STATUE_USE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<String>> STATUE_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<String>> STATUE_BLACKLIST;

    public static final List<String> DEFAULT_WHITELIST = new ArrayList<>();

    public static final List<String> DEFAULT_BLACKLIST = new ArrayList<>();

    public static void init(ForgeConfigSpec.Builder serverBuilder) {
        DEFAULT_WHITELIST.add("minecraft:overworld");
        SPAWN_PROB = serverBuilder.comment("Weighted probability of a group of Rocket Squids spawning.")
                .defineInRange("spawn.prob", 4, 1, 100);
        MIN_GROUP_SIZE = serverBuilder.comment("Smallest possible size of a group.")
                .defineInRange("spawn.min", 2, 1, 20);
        MAX_GROUP_SIZE = serverBuilder.comment("Largest possible size of a group.")
                .defineInRange("spawn.max", 5, 1, 40);
        CONCH_USE_WHITELIST = serverBuilder.comment("If true, uses the conch whitelist. If false, uses the blacklist.")
                .define("worldgen.conch.usewhitelist", true);
        CONCH_WHITELIST = serverBuilder.comment("The list of dimensions where conches can appear.")
                .define("worldgen.conch.whitelist", DEFAULT_WHITELIST);
        CONCH_BLACKLIST = serverBuilder.comment("The list of dimensions where conches cannot appear.")
                .define("worldgen.conch.blacklist", DEFAULT_BLACKLIST);
        STATUE_USE_WHITELIST = serverBuilder.comment("If true, uses the statue whitelist. If false, uses the blacklist.")
                .define("worldgen.statue.usewhitelist", true);
        STATUE_WHITELIST = serverBuilder.comment("The list of dimensions where statues can appear.")
                .define("worldgen.statue.whitelist", DEFAULT_WHITELIST);
        STATUE_BLACKLIST = serverBuilder.comment("The list of dimensions where statues cannot appear.")
                .define("worldgen.statue.blacklist", DEFAULT_BLACKLIST);
    }
}
