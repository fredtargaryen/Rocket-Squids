/**
 * Add synchronisation
 * Some kind of not responding issue?
 * Test EntityAIGiveUp
 * Test EntityAISwimAround
 * Hitting a squid makes it go flying (check)
 * Re-enable spin (remove entity AI to test)
 * Fire for blasting rocket squids (check)
 * Nitro ink sacs
 */

package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.item.*;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.proxy.CommonProxy;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
public class RocketSquidsBase
{
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(DataReference.MODID)
    public static RocketSquidsBase instance;
	
    /**
     * Declare all items here
     */
    public static Item nitroinksac;
	
    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
	
    public static CommonProxy proxy;
        
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	nitroinksac = new ItemNitroInkSac()
    	.setMaxStackSize(64)
        .setUnlocalizedName("nitroinksac")
        .setRegistryName("nitroinksac");

        proxy.registerRenderers();
    }
        
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        //Register Entities with EntityRegistry
        EntityRegistry.addSpawn(EntityRocketSquid.class, 7, 1, 3, EnumCreatureType.WATER_CREATURE,
                Biomes.DEEP_OCEAN, Biomes.OCEAN, Biomes.RIVER, Biomes.SWAMPLAND);
        //Last three params are for tracking: trackingRange, updateFrequency and sendsVelocityUpdates
        EntityRegistry.registerModEntity(EntityRocketSquid.class, "rocketsquid", 0, instance, 64, 10, true);
        EntityRegistry.registerEgg(EntityRocketSquid.class, 9838110, 16744192);
        GameRegistry.register(nitroinksac);

        proxy.registerModels();
        MessageHandler.init();
    }
        
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        OreDictionary.registerOre("itemGunpowder", nitroinksac);
    }
}