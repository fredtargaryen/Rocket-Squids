package com.fredtargaryen.rocketsquids;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessagePlayNoteClient: When received on the client, the note is played
 * MessagePlayNoteServer: sent to server when a note is played. When received, a MessagePlayNoteClient is sent to everyone nearby
 * MessageSquidCapData: The ISquidCapability data for Rocket Squids
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
    public static final String VERSION = "1.1";

    public static final ResourceLocation SQUELEPORTER_LOCATION = new ResourceLocation(DataReference.MODID, "isqueleporter");
    public static final ResourceLocation SQUID_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "isquidcapability");

    public static EnumFacing randomHorizontalFacing(Random rand) {
        switch(rand.nextInt(4)) {
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.EAST;
            case 2:
                return EnumFacing.SOUTH;
            case 3:
                return EnumFacing.WEST;
        }
        return EnumFacing.NORTH;
    }
}
