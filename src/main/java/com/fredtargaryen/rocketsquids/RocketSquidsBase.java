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
import com.fredtargaryen.rocketsquids.item.capability.DefaultSqueleporterImplFactory;
import com.fredtargaryen.rocketsquids.item.capability.ISqueleporter;
import com.fredtargaryen.rocketsquids.item.capability.SqueleporterCapStorage;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.proxy.CommonProxy;
import com.fredtargaryen.rocketsquids.worldgen.ConchGen;
import com.fredtargaryen.rocketsquids.worldgen.StatueGen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
@Mod.EventBusSubscriber
public class RocketSquidsBase {
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(DataReference.MODID)
    public static RocketSquidsBase instance;

    //CONFIG VARS
    private static int spawnProb;
    private static int minGrpSize;
    private static int maxGrpSize;

    //Declare all blocks here
    @ObjectHolder("blockconch")
    public static Block blockConch;
    @ObjectHolder("statue")
    public static Block blockStatue;

    //Declare all items here
    @ObjectHolder("conch")
    public static Item itemConch;
    @ObjectHolder("conchtwo")
    public static Item itemConch2;
    @ObjectHolder("conchthree")
    public static Item itemConch3;
    @ObjectHolder("nitroinksac")
    public static Item nitroinksac;
    @ObjectHolder("turbotube")
    public static Item turbotube;
    @ObjectHolder("statue")
    public static Item iStatue;
    @ObjectHolder("squavigator")
    public static Item squavigator;
    @ObjectHolder("squeleporter")
    public static Item squeleporter;

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
        CapabilityManager.INSTANCE.register(ISqueleporter.class, new SqueleporterCapStorage(), new DefaultSqueleporterImplFactory());
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

        squavigator = new Item()
                .setMaxStackSize(1)
                .setUnlocalizedName("squavigator")
                .setRegistryName("squavigator");

        squeleporter = new ItemSqueleporter()
                .setMaxStackSize(1)
                .setUnlocalizedName("squeleporter")
                .setRegistryName("squeleporter");

        //Making Creative Tab
        squidsTab = new CreativeTabs(CreativeTabs.getNextID(), "rocketsquidsft") {
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
        squavigator.setCreativeTab(RocketSquidsBase.squidsTab);
        squeleporter.setCreativeTab(RocketSquidsBase.squidsTab);
        blockStatue.setCreativeTab(RocketSquidsBase.squidsTab);
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
        event.getRegistry().registerAll(itemConch, itemConch2, itemConch3, nitroinksac, turbotube, iStatue,
                squavigator, squeleporter);
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

    /**
     * Code for the Squeleporter capability
     */
    @CapabilityInject(ISqueleporter.class)
    public static final Capability<ISqueleporter> SQUELEPORTER = null;

    @SubscribeEvent
    public void onItemStackConstruct(AttachCapabilitiesEvent<ItemStack> evt) {
        if(evt.getObject().getItem() == squeleporter) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION,
                    new ICapabilitySerializable<NBTTagCompound>() {
                        ISqueleporter inst = SQUELEPORTER.getDefaultInstance();

                        @Override
                        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                            return capability == SQUELEPORTER;
                        }

                        @Nullable
                        @Override
                        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                            return capability == SQUELEPORTER ? SQUELEPORTER.<T>cast(inst) : null;
                        }

                        @Override
                        public NBTTagCompound serializeNBT() {
                            return (NBTTagCompound) SQUELEPORTER.getStorage().writeNBT(SQUELEPORTER, inst, null);
                        }

                        @Override
                        public void deserializeNBT(NBTTagCompound nbt) {
                            SQUELEPORTER.getStorage().readNBT(SQUELEPORTER, inst, null, nbt);
                        }
                    });
        }
    }

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

    ////////////////////////
    //FOR THE MODID CHANGE//
    ////////////////////////
    @SubscribeEvent
    public void handleMissingMappings(RegistryEvent.MissingMappings evt) {
        String fullName = evt.getName().toString();
        if(fullName.equals("minecraft:blocks")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if(trueMapping.key.getResourceDomain().equals("ftrsquids")) {
                    switch (trueMapping.key.getResourcePath()) {
                        case "blockconch":
                            trueMapping.remap(blockConch);
                            break;
                        case "statue":
                            trueMapping.remap(blockStatue);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:entities")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if(trueMapping.key.getResourceDomain().equals("ftrsquids")) {
                    EntityRegistry entityRegistry = EntityRegistry.instance();
                    switch (trueMapping.key.getResourcePath()) {
                        case "turbotube":
                            trueMapping.remap(EntityRegistry.getEntry(EntityThrownTube.class));
                            break;
                        case "babyrocketsquid":
                            trueMapping.remap(EntityRegistry.getEntry(EntityBabyRocketSquid.class));
                            break;
                        case "rocketsquid":
                            trueMapping.remap(EntityRegistry.getEntry(EntityRocketSquid.class));
                            break;
                        case "nitroinksac":
                            trueMapping.remap(EntityRegistry.getEntry(EntityThrownSac.class));
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:items")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getResourceDomain().equals("ftrsquids")) {
                    switch (trueMapping.key.getResourcePath()) {
                        case "conch":
                            trueMapping.remap(itemConch);
                            break;
                        case "conchtwo":
                            trueMapping.remap(itemConch2);
                            break;
                        case "conchthree":
                            trueMapping.remap(itemConch3);
                            break;
                        case "nitroinksac":
                            trueMapping.remap(nitroinksac);
                            break;
                        case "turbotube":
                            trueMapping.remap(turbotube);
                            break;
                        case "statue":
                            trueMapping.remap(iStatue);
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:soundevents")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getResourceDomain().equals("ftrsquids")) {
                    String soundName = trueMapping.key.getResourcePath();
                    if(soundName.equals("blastoff")) trueMapping.remap(Sounds.BLASTOFF);
                    else {
                        for (int i = 0; i < 36; ++i) {
                            //Check the note name (e.g. "concha#5") of the missing mapping is equal to the note name of any notes
                            if (soundName.equals(Sounds.CONCH_NOTES[i].getSoundName().getResourcePath())) {
                                trueMapping.remap(Sounds.CONCH_NOTES[i]);
                            }
                        }
                    }
                }
            }
        }
    }
}