/**
 * Pre-release tasks:
 * Rocket squid moved too quickly. Looks like [2.9375,2.96875) right now; trying 2.952 (check 1 on server)
 *
 * Post-release tasks:
 * Squid firework effect in explosion
 * Good-sized non-griefing explosion for Tubes
 * Think about rider interaction; quite difficult right now
 * Try to prevent "vehicle moved too quickly!" so blast speed can be increased
 * VIP riders
 */

package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import com.fredtargaryen.rocketsquids.entity.capability.DefaultSquidImplFactory;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import com.fredtargaryen.rocketsquids.entity.capability.SquidCapStorage;
import com.fredtargaryen.rocketsquids.item.*;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.proxy.CommonProxy;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

    //CONFIG VARS
    private static int spawnProb;
    private static int minGrpSize;
    private static int maxGrpSize;

    /**
     * Declare all items here
     */
    public static Item nitroinksac;
    public static Item turbotube;
	
    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
	
    public static CommonProxy proxy;

    //Code for the BeRocketSquid capability
    @CapabilityInject(ISquidCapability.class)
    public static final Capability<ISquidCapability> SQUIDCAP = null;

    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent.Entity evt)
    {
        if(evt.getEntity() instanceof EntityRocketSquid) {
            evt.addCapability(DataReference.SQUID_CAP_LOCATION,
                    //Full name ICapabilitySerializableProvider
                    new ICapabilitySerializable<NBTTagCompound>()
                    {
                        ISquidCapability inst = SQUIDCAP.getDefaultInstance();

                        @Override
                        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                            return capability == SQUIDCAP;
                        }

                        @Override
                        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                            return capability == SQUIDCAP ? SQUIDCAP.<T>cast(inst) : null;
                        }

                        @Override
                        public NBTTagCompound serializeNBT() {
                            return (NBTTagCompound) SQUIDCAP.getStorage().writeNBT(SQUIDCAP, inst, null);
                        }

                        @Override
                        public void deserializeNBT(NBTTagCompound nbt) {
                            SQUIDCAP.getStorage().readNBT(SQUIDCAP, inst, null, nbt);
                        }
                    }
            );
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //CONFIG SETUP
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        spawnProb = config.getInt("spawnProb", "Spawning", 7, 1, 100, "Weighted probability of a group spawning");
        minGrpSize = config.getInt("minGroupSize", "Spawning", 2, 1, 20, "Smallest possible size of a group");
        maxGrpSize = config.getInt("maxGroupSize", "Spawning", 5, 1, 40, "Largest possible size of a group");
        config.save();
        if(maxGrpSize < minGrpSize)
        {
            maxGrpSize = minGrpSize;
        }

    	nitroinksac = new ItemNitroInkSac()
    	        .setMaxStackSize(64)
                .setUnlocalizedName("nitroinksac")
                .setRegistryName("nitroinksac");

        turbotube = new ItemTurboTube()
                .setMaxStackSize(64)
                .setUnlocalizedName("turbotube")
                .setRegistryName("turbotube");

        proxy.registerRenderers();

        //Capability
        CapabilityManager.INSTANCE.register(ISquidCapability.class, new SquidCapStorage(), new DefaultSquidImplFactory());
        MinecraftForge.EVENT_BUS.register(this);
    }
        
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        //Register Entities with EntityRegistry
        //Last three params are for tracking: trackingRange, updateFrequency and sendsVelocityUpdates
        EntityRegistry.registerModEntity(EntityRocketSquid.class, "rocketsquid", 0, instance, 64, 10, true);
        EntityRegistry.registerModEntity(EntityThrownSac.class, "nitroinksac", 1, instance, 64, 10, true);
        EntityRegistry.registerModEntity(EntityThrownTube.class, "turbotube", 2, instance, 64, 10, true);

        //Other Rocket Squid info
        EntityRegistry.addSpawn(EntityRocketSquid.class, spawnProb, minGrpSize, maxGrpSize, EnumCreatureType.WATER_CREATURE,
                Biomes.DEEP_OCEAN, Biomes.OCEAN, Biomes.RIVER, Biomes.SWAMPLAND);
        EntitySpawnPlacementRegistry.setPlacementType(EntityRocketSquid.class, EntityLiving.SpawnPlacementType.IN_WATER);
        EntityRegistry.registerEgg(EntityRocketSquid.class, 9838110, 16744192);

        //Register items
        GameRegistry.register(nitroinksac);
        GameRegistry.register(turbotube);

        proxy.registerModels();
        MessageHandler.init();
    }
        
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        OreDictionary.registerOre("gunpowder", turbotube);
        OreDictionary.registerOre("dyeOrange", nitroinksac);
    }
}