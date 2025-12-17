package com.fredtargaryen.rocketsquids;

import net.minecraft.core.Direction;
import 	net.minecraft.resources.ResourceLocation;

import java.util.Random;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessageAdultCapData: The capability for adult Rocket Squids
 * MessageBabyCapData: The capability for baby Rocket Squids
 * MessagePlayNoteClient: When received on the client, the note is played
 * MessagePlayNoteServer: sent to server when a note is played. When received, a MessagePlayNoteClient is sent to everyone nearby
 * MessageSquidNote: sent to clients whose players are wearing conches, when a Rocket Squid is broadcasting a note
 * <p>
 * When changing version number, change in: build.gradle, mods.toml
 * <p>
 * Superflat preset for squid testing:
 * minecraft:glowstone,42*minecraft:water
 */
public class DataReference {
    //MAIN MOD DETAILS
    public static final String MODID = "rocketsquids";
    public static final String MODNAME = "Rocket Squids Relight";

    public static final ResourceLocation SQUELEPORTER_LOCATION = new ResourceLocation(DataReference.MODID, "isqueleporter");
    public static final ResourceLocation BABY_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "ibaby");
    public static final ResourceLocation ADULT_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "iadult");

    //////////////////////
    //Worldgen constants//
    //////////////////////
    public static Direction randomHorizontalFacing(Random rand) {
        return switch (rand.nextInt(4)) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }
}
