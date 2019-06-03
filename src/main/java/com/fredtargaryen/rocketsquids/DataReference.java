package com.fredtargaryen.rocketsquids;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * Superflat preset for squid testing:
 * 3;26*minecraft:glowstone,8*minecraft:water;1
 */
public class DataReference {
    //MAIN MOD DETAILS
    public static final String MODID = "rocketsquidsft";
    public static final String MODNAME = "Rocket Squids";
    public static final String VERSION = "1.1";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ServerProxy";

    public static final ResourceLocation SQUELEPORTER_LOCATION = new ResourceLocation(DataReference.MODID, "ISqueleporter");
    public static final ResourceLocation SQUID_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "ISquidCapability");

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
