package com.fredtargaryen.rocketsquids;

import net.minecraft.util.ResourceLocation;

/**
 * Superflat preset for squid testing:
 * 3;26*minecraft:glowstone,8*minecraft:water;1
 */
public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftrsquids";
    public static final String MODNAME = "Rocket Squids";
    public static final String VERSION = "0.9.1";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ServerProxy";

    public static final ResourceLocation SQUID_CAP_LOCATION = new ResourceLocation(DataReference.MODID, "ISquidCapability");
}
