package com.fredtargaryen.rocketsquids;

/**
 * Superflat preset for squid testing:
 *3;minecraft:glowstone,5*minecraft:water;1
 * ===DESCRIPTION OF CHANNELS===
 */

public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftrsquids";
    public static final String MODNAME = "Rocket Squids";
    public static final String VERSION = "0.1";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.rocketsquids.proxy.ServerProxy";

    public static String resPath(String un)
    {
        return MODID+":"+un.substring(5);
    }
}
