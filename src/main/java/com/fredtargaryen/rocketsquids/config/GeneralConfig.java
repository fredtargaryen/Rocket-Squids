package com.fredtargaryen.rocketsquids.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class GeneralConfig {
    public static ForgeConfigSpec.IntValue SPAWN_PROB;

    public static ForgeConfigSpec.IntValue MIN_GROUP_SIZE;

    public static ForgeConfigSpec.IntValue MAX_GROUP_SIZE;

    public static ForgeConfigSpec.IntValue STATUE_FREQUENCY;

    public static void init(ForgeConfigSpec.Builder serverBuilder) {
        SPAWN_PROB = serverBuilder.comment("Weighted probability of a group spawning.")
                .defineInRange("spawn.prob", 4, 1, 100);
        MIN_GROUP_SIZE = serverBuilder.comment("Smallest possible size of a group.")
                .defineInRange("spawn.min", 2, 1, 20);
        MAX_GROUP_SIZE = serverBuilder.comment("Largest possible size of a group")
                .defineInRange("spawn.max", 5, 1, 40);
        STATUE_FREQUENCY = serverBuilder.comment("One statue will appear in every nxn chunk area. Changing this in an existing world is not recommended.")
                .defineInRange("worldgen.statue.frequency", 32, 8, 2000);
    }
}
