/**
 * TODO
 * Squid notes are wrong
 * * See if high-quality stretching keeps the duration the same or not
 * * Otherwise attempt sliding shift + change pitch
 * * Otherwise try decimals in sliding scale
 * * Otherwise keep I guess
 * Conch rotating with body but not head
 * * Check Minecraft Forum
 */
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.block.BlockConch;
import com.fredtargaryen.rocketsquids.block.BlockStatue;
import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import com.fredtargaryen.rocketsquids.entity.capability.DefaultSquidImplFactory;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import com.fredtargaryen.rocketsquids.entity.capability.SquidCapStorage;
import com.fredtargaryen.rocketsquids.item.*;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.proxy.CommonProxy;
import com.fredtargaryen.rocketsquids.worldgen.ConchGen;
import com.fredtargaryen.rocketsquids.worldgen.StatueGen;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
@Mod.EventBusSubscriber
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
     * Declare all blocks here
     */
    public static Block blockConch;
    public static Block blockStatue;

    /**
     * Declare all items here
     */
    public static Item itemConch;
    public static Item itemConch2;
    public static Item itemConch3;
    public static Item nitroinksac;
    public static Item turbotube;
    public static Item iStatue;

    public static CreativeTabs squidsTab;

    /**
     * A custom firework that looks like a Rocket Squid.
     * Firework structure:
     * TagCompound          (firework)
     * |_TagList            (list, "Explosions")
     *   |_TagCompound      (Single firework part)
     *     |_TagBoolean     ("Trail")
     *     |_TagBoolean     ("Flicker")
     *     |_TagIntArray    ("Colors")
     *     |_TagIntArray    ("FadeColors")
     */
    public static final NBTTagCompound firework = new NBTTagCompound();

    private static ConchGen conchGen;
    private static StatueGen statueGen;
	
    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Capability
        CapabilityManager.INSTANCE.register(ISquidCapability.class, new SquidCapStorage(), new DefaultSquidImplFactory());
        MinecraftForge.EVENT_BUS.register(this);

        //CONFIG SETUP
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        spawnProb = config.getInt("spawnProb", "Spawning", 4, 1, 100, "Weighted probability of a group spawning");
        minGrpSize = config.getInt("minGroupSize", "Spawning", 2, 1, 20, "Smallest possible size of a group");
        maxGrpSize = config.getInt("maxGroupSize", "Spawning", 5, 1, 40, "Largest possible size of a group");
        config.save();
        if(maxGrpSize < minGrpSize)
        {
            maxGrpSize = minGrpSize;
        }

        //Making blocks
        blockConch = new BlockConch()
                .setUnlocalizedName("blockconch")
                .setRegistryName("blockconch");

        blockStatue = new BlockStatue(Material.ROCK)
                .setUnlocalizedName("statue")
                .setRegistryName("statue");

        //Making items
        itemConch = new ItemConch()
                .setMaxStackSize(4)
                .setUnlocalizedName("conch")
                .setRegistryName("conch");

        itemConch2 = new ItemConch2()
                .setMaxStackSize(1)
                .setUnlocalizedName("conchtwo")
                .setRegistryName("conchtwo");

        itemConch3 = new ItemConch3()
                .setMaxStackSize(1)
                .setUnlocalizedName("conchthree")
                .setRegistryName("conchthree");

    	nitroinksac = new ItemNitroInkSac()
    	        .setMaxStackSize(64)
                .setUnlocalizedName("nitroinksac")
                .setRegistryName("nitroinksac");

        turbotube = new ItemTurboTube()
                .setMaxStackSize(64)
                .setUnlocalizedName("turbotube")
                .setRegistryName("turbotube");

        iStatue = new ItemBlock(blockStatue)
                .setMaxStackSize(1)
                .setUnlocalizedName("statue")
                .setRegistryName("statue");

        //Making Creative Tab
        squidsTab = new CreativeTabs(CreativeTabs.getNextID(), "ftrsquids") {
            ItemStack conch = new ItemStack(itemConch);

            @Override
            public ItemStack getTabIconItem() {
                return this.conch;
            }
        };
        itemConch.setCreativeTab(RocketSquidsBase.squidsTab);
        itemConch2.setCreativeTab(RocketSquidsBase.squidsTab);
        itemConch3.setCreativeTab(RocketSquidsBase.squidsTab);
        nitroinksac.setCreativeTab(RocketSquidsBase.squidsTab);
        turbotube.setCreativeTab(RocketSquidsBase.squidsTab);

        Sounds.constructAndRegisterSoundEvents();

        proxy.registerRenderers();

        //Make the firework
        NBTTagList list = new NBTTagList();
            NBTTagCompound f1 = new NBTTagCompound();
            f1.setBoolean("Flicker", false);
            f1.setBoolean("Trail", false);
            f1.setIntArray("Colors", new int[]{15435844});
            f1.setIntArray("FadeColors", new int[]{6719955});
        list.appendTag(f1);
        firework.setTag("Explosions", list);
    }

    ///////////////////
    //REGISTRY EVENTS//
    ///////////////////

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(blockConch, blockStatue);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(itemConch, itemConch2, itemConch3, nitroinksac, turbotube, iStatue);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        proxy.registerModels();
    }
        
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Register Entities with EntityRegistry
        ResourceLocation squidResourceLocation = new ResourceLocation(DataReference.MODID+":rocketsquid");
        ResourceLocation babySquidResourceLocation = new ResourceLocation(DataReference.MODID+":babyrocketsquid");
        //Last three params are for tracking: trackingRange, updateFrequency and sendsVelocityUpdates
        EntityRegistry.registerModEntity(squidResourceLocation, EntityRocketSquid.class, "rocketsquid", 0, instance, 128, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(DataReference.MODID+":nitroinksac"), EntityThrownSac.class, "nitroinksac", 1, instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(DataReference.MODID+":turbotube"), EntityThrownTube.class, "turbotube", 2, instance, 64, 10, true);
        EntityRegistry.registerModEntity(babySquidResourceLocation, EntityBabyRocketSquid.class, "babyrs", 4, instance, 64, 10, true);

        //Other Rocket Squid info
        EntityRegistry.addSpawn(EntityRocketSquid.class, spawnProb, minGrpSize, maxGrpSize, EnumCreatureType.WATER_CREATURE,
                Biomes.DEEP_OCEAN, Biomes.OCEAN, Biomes.RIVER, Biomes.SWAMPLAND);
        EntitySpawnPlacementRegistry.setPlacementType(EntityRocketSquid.class, EntityLiving.SpawnPlacementType.IN_WATER);
        EntityRegistry.registerEgg(squidResourceLocation, 9838110, 16744192);

        conchGen = new ConchGen();
        GameRegistry.registerWorldGenerator(conchGen, 3);
        statueGen = new StatueGen();
        GameRegistry.registerWorldGenerator(statueGen, 3);

        proxy.registerModels();
        MessageHandler.init();
    }
        
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        OreDictionary.registerOre("gunpowder", turbotube);
        OreDictionary.registerOre("dyeOrange", nitroinksac);
    }

    /////////////////
    //CAPABILIITIES//
    /////////////////

    /**
     * Code for the BeRocketSquid capability
     */
    @CapabilityInject(ISquidCapability.class)
    public static final Capability<ISquidCapability> SQUIDCAP = null;

    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent<Entity> evt)
    {
        if(evt.getObject() instanceof EntityRocketSquid) {
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
}