/**
 * Squids turn and swim in negative yaw direction
 * Need smaller chance of shaking in EntityAISwimAround
 * Fire for blasting rocket squids (check)
 * Re-enable spin (remove entity AI to test)
 * Nitro ink sacs
 */

package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
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
	
    /**
     * Declare all items here
     */
    public static Item nitroinksac;
	
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
    	nitroinksac = new ItemNitroInkSac()
    	.setMaxStackSize(64)
        .setUnlocalizedName("nitroinksac")
        .setRegistryName("nitroinksac");

        proxy.registerRenderers();

        //Capability
        CapabilityManager.INSTANCE.register(ISquidCapability.class, new SquidCapStorage(), new DefaultSquidImplFactory());
        MinecraftForge.EVENT_BUS.register(this);
    }
        
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        //Register Entities with EntityRegistry
        EntityRegistry.addSpawn(EntityRocketSquid.class, 7, 1, 3, EnumCreatureType.WATER_CREATURE,
                Biomes.DEEP_OCEAN, Biomes.OCEAN, Biomes.RIVER, Biomes.SWAMPLAND);
        EntitySpawnPlacementRegistry.setPlacementType(EntityRocketSquid.class, EntityLiving.SpawnPlacementType.IN_WATER);
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