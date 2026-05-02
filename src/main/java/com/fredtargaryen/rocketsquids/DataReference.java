// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

/**
 * When changing version number, change mod_version in gradle.properties
 * Superflat preset for squid testing:
 * minecraft:glowstone,42*minecraft:water
 */
@SuppressWarnings("removal")
public class DataReference {
    // MAIN MOD DETAILS
    public static final String MODID = "rocketsquids";
    public static final String MODNAME = "Rocket Squids";

    public static ResourceLocation getResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Conch hearing ranges
    public static final double PLAYER_HEAR_RANGE = 16.0;
    public static final double SQUID_SING_RANGE = 8.0;
    public static final double SQUID_LISTEN_RANGE = 8.0;

    //////////////////////
    //Worldgen constants//
    //////////////////////
    public static Direction randomHorizontalFacing(RandomSource rand) {
        return switch (rand.nextInt(4)) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            default -> Direction.WEST;
        };
    }
}
