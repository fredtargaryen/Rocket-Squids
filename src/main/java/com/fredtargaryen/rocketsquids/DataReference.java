package com.fredtargaryen.rocketsquids;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessagePlayNoteClient: When received on the client, the note is played
 * MessagePlayNoteServer: sent to server when a note is played. When received, a MessagePlayNoteClient is sent to everyone nearby
 * MessageAdultCapData: The capability for adult Rocket Squids
 * MessageBabyCapData: The capability for baby Rocket Squids
 * MessageSquidNote: sent to clients whose players are wearing conches, when a Rocket Squid is broadcasting a note
 *
 * When changing version number, change in: DataReference, build.gradle, mods.toml
 *
 * Superflat preset for squid testing:
 * 3;26*minecraft:glowstone,8*minecraft:water;1
 */
public class DataReference {
    //MAIN MOD DETAILS
    public static final String MODID = "rocketsquidsft";
    public static final String MODNAME = "Rocket Squids";
    public static final String VERSION = "1.1.1";

    public static final ResourceLocation SQUELEPORTER_LOCATION = new ResourceLocation(DataReference.MODID, "isqueleporter");
    public static final ResourceLocation BABY_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "ibaby");
    public static final ResourceLocation ADULT_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "iadult");

    //////////////////////
    //Worldgen constants//
    //////////////////////
    /**
     * A chunk area is a square region of chunks in which one statue will generate.
     */
    public static final int CHUNK_AREA_SIZE = 32;

    public static Direction randomHorizontalFacing(Random rand) {
        switch(rand.nextInt(4)) {
            case 0:
                return Direction.NORTH;
            case 1:
                return Direction.EAST;
            case 2:
                return Direction.SOUTH;
            case 3:
                return Direction.WEST;
        }
        return Direction.NORTH;
    }
}
