/**
 * CHECKLIST
 * Do conches get waterlogged properly?
 * Can blocks render through statues?
 * Are squid fires bright enough?
 * Do sacs and tubes look right?
 * Does crafting with gunpowder and dye work? (Optional, as long as no errors on startup)
 */
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.block.BlockConch;
import com.fredtargaryen.rocketsquids.block.BlockStatue;
import com.fredtargaryen.rocketsquids.config.Config;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
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
import com.fredtargaryen.rocketsquids.proxy.ClientProxy;
import com.fredtargaryen.rocketsquids.proxy.IProxy;
import com.fredtargaryen.rocketsquids.proxy.ServerProxy;
import com.fredtargaryen.rocketsquids.worldgen.ConchGen;
import com.fredtargaryen.rocketsquids.worldgen.FeatureManager;
import com.fredtargaryen.rocketsquids.worldgen.StatueGen;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(value = DataReference.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RocketSquidsBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //Declare all blocks here
    @ObjectHolder("conch")
    public static Block BLOCK_CONCH;
    @ObjectHolder("statue")
    public static Block BLOCK_STATUE;

    //Declare all items here
    @ObjectHolder("conch")
    public static Item ITEM_CONCH;
    @ObjectHolder("conchtwo")
    public static Item ITEM_CONCH2;
    @ObjectHolder("conchthree")
    public static Item ITEM_CONCH3;
    @ObjectHolder("nitroinksac")
    public static Item NITRO_SAC;
    @ObjectHolder("turbotube")
    public static Item TURBO_TUBE;
    @ObjectHolder("statue")
    public static Item ITEM_STATUE;
    @ObjectHolder("squavigator")
    public static Item SQUAVIGATOR;
    @ObjectHolder("squeleporter")
    public static Item SQUELEPORTER;
    @ObjectHolder("egg")
    public static Item SQUID_EGG;

    //Declare all EntityTypes here
    @ObjectHolder("nitroinksac")
    public static EntityType SAC_TYPE;
    @ObjectHolder("turbotube")
    public static EntityType TUBE_TYPE;
    @ObjectHolder("rocketsquid")
    public static EntityType SQUID_TYPE;
    @ObjectHolder("babyrs")
    public static EntityType BABY_SQUID_TYPE;
    @ObjectHolder("primala")
    public static EntityType PRIMAL_A_TYPE;

    /**
     * The creative tab for all items from Rocket Squids.
     */
    public static ItemGroup SQUIDS_TAB = new ItemGroup(DataReference.MODID) {
        @Override
        public ItemStack createIcon() {
            return ITEM_CONCH.getDefaultInstance();
        }
    };

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
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public RocketSquidsBase() {
        //Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG_SPEC);

        //Event bus
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Load the config
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(DataReference.MODID + ".toml"));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockConch()
                        .setRegistryName("conch"),
                new BlockStatue()
                        .setRegistryName("statue")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemConch()
                        .setRegistryName("conch"),
                new ItemConch2()
                        .setRegistryName("conchtwo"),
                new ItemConch3()
                        .setRegistryName("conchthree"),
                new ItemNitroInkSac()
                        .setRegistryName("nitroinksac"),
                new ItemTurboTube()
                        .setRegistryName("turbotube"),
                new ItemBlock(BLOCK_STATUE, new Item.Properties().group(SQUIDS_TAB).maxStackSize(1))
                        .setRegistryName("statue"),
                new Item(new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB).maxStackSize(1))
                        .setRegistryName("squavigator"),
                new ItemSqueleporter()
                        .setRegistryName("squeleporter"),
                new ItemSpawnEgg(SQUID_TYPE,9838110, 16744192, new Item.Properties())
                        .setRegistryName("egg")
        );
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create(EntityRocketSquid.class, EntityRocketSquid::new)
                        .tracker(128, 10, true)
                        .build(DataReference.MODID)
                        .setRegistryName(new ResourceLocation(DataReference.MODID, "rocketsquid")),
                EntityType.Builder.create(EntityBabyRocketSquid.class, EntityBabyRocketSquid::new)
                        .tracker(64, 10, true)
                        .build(DataReference.MODID)
                        .setRegistryName(new ResourceLocation(DataReference.MODID, "babyrs")),
                EntityType.Builder.create(EntityThrownSac.class, EntityThrownSac::new)
                        .tracker(64, 10, true)
                        .build(DataReference.MODID)
                        .setRegistryName(new ResourceLocation(DataReference.MODID, "nitroinksac")),
                EntityType.Builder.create(EntityThrownTube.class, EntityThrownTube::new)
                        .tracker(64, 10, true)
                        .build(DataReference.MODID)
                        .setRegistryName(new ResourceLocation(DataReference.MODID, "turbotube"))
        );
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        Sounds.constructAndRegisterSoundEvents(event);
        event.getRegistry().registerAll(
                new SoundEvent(new ResourceLocation(DataReference.MODID, "greened")).setRegistryName("greened"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "tp")).setRegistryName("tp"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "flick")).setRegistryName("flick"));
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        MessageHandler.init();

        //Capability
        CapabilityManager.INSTANCE.register(ISquidCapability.class, new SquidCapStorage(), new DefaultSquidImplFactory());
        CapabilityManager.INSTANCE.register(ISqueleporter.class, new SqueleporterCapStorage(), new DefaultSqueleporterImplFactory());
        MinecraftForge.EVENT_BUS.register(this);

        proxy.registerRenderers();

        //Make the firework
        NBTTagList list = new NBTTagList();
            NBTTagCompound f1 = new NBTTagCompound();
            f1.setBoolean("Flicker", false);
            f1.setBoolean("Trail", false);
            f1.setIntArray("Colors", new int[]{15435844});
            f1.setIntArray("FadeColors", new int[]{6719955});
        list.add(f1);
        firework.setTag("Explosions", list);


        //Other Rocket Squid info
        EntitySpawnPlacementRegistry.register(SQUID_TYPE, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.OCEAN_FLOOR, null);
        Biomes.DEEP_OCEAN.getSpawns(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(SQUID_TYPE, GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get()));
        Biomes.OCEAN.getSpawns(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(SQUID_TYPE, GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get()));
        Biomes.RIVER.getSpawns(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(SQUID_TYPE, GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get()));
        Biomes.SWAMP.getSpawns(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(SQUID_TYPE, GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get()));
        Biomes.FROZEN_OCEAN.getSpawns(EnumCreatureType.WATER_CREATURE).add(new Biome.SpawnListEntry(SQUID_TYPE, GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get()));

        new FeatureManager().registerGenerators();
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
    public static final Capability<ISqueleporter> SQUELEPORTER_CAP = null;

    @SubscribeEvent
    public void onItemStackConstruct(AttachCapabilitiesEvent<ItemStack> evt) {
        if(evt.getObject().getItem() == SQUELEPORTER) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION,
                    new ICapabilitySerializable<NBTTagCompound>() {
                        ISqueleporter inst = SQUELEPORTER_CAP.getDefaultInstance();

                        @Nullable
                        @Override
                        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                            return capability == SQUELEPORTER_CAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                        }

                        @Override
                        public NBTTagCompound serializeNBT() {
                            return (NBTTagCompound) SQUELEPORTER_CAP.getStorage().writeNBT(SQUELEPORTER_CAP, inst, null);
                        }

                        @Override
                        public void deserializeNBT(NBTTagCompound nbt) {
                            SQUELEPORTER_CAP.getStorage().readNBT(SQUELEPORTER_CAP, inst, null, nbt);
                        }
                    });
        }
    }

    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent<Entity> evt) {
        if(evt.getObject() instanceof EntityRocketSquid) {
            evt.addCapability(DataReference.SQUID_CAP_LOCATION,
                    //Full name ICapabilitySerializableProvider
                    new ICapabilitySerializable<NBTTagCompound>() {
                        ISquidCapability inst = SQUIDCAP.getDefaultInstance();

                        @Override
                        public <T> LazyOptional<T> getCapability(Capability<T> capability, EnumFacing facing) {
                            return capability == SQUIDCAP ? LazyOptional.of(() -> (T)inst) : LazyOptional.empty();
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
                if(trueMapping.key.getNamespace().equals("ftrsquids")) {
                    switch (trueMapping.key.getPath()) {
                        case "blockconch":
                            trueMapping.remap(BLOCK_CONCH);
                            break;
                        case "statue":
                            trueMapping.remap(BLOCK_STATUE);
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
                if(trueMapping.key.getNamespace().equals("ftrsquids")) {
                    switch (trueMapping.key.getPath()) {
                        case "turbotube":
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(TUBE_TYPE.getRegistryName()));
                            break;
                        case "babyrocketsquid":
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(BABY_SQUID_TYPE.getRegistryName()));
                            break;
                        case "rocketsquid":
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(SQUID_TYPE.getRegistryName()));
                            break;
                        case "nitroinksac":
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(SAC_TYPE.getRegistryName()));
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:items")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getNamespace().equals("ftrsquids")) {
                    switch (trueMapping.key.getPath()) {
                        case "conch":
                            trueMapping.remap(ITEM_CONCH);
                            break;
                        case "conchtwo":
                            trueMapping.remap(ITEM_CONCH2);
                            break;
                        case "conchthree":
                            trueMapping.remap(ITEM_CONCH3);
                            break;
                        case "nitroinksac":
                            trueMapping.remap(NITRO_SAC);
                            break;
                        case "turbotube":
                            trueMapping.remap(TURBO_TUBE);
                            break;
                        case "statue":
                            trueMapping.remap(ITEM_STATUE);
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:soundevents")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getNamespace().equals("ftrsquids")) {
                    String soundName = trueMapping.key.getPath();
                    if(soundName.equals("blastoff")) trueMapping.remap(Sounds.BLASTOFF);
                    else {
                        for (int i = 0; i < 36; ++i) {
                            //Check the note name (e.g. "concha#5") of the missing mapping is equal to the note name of any notes
                            if (soundName.equals(Sounds.CONCH_NOTES[i].getName().getPath())) {
                                trueMapping.remap(Sounds.CONCH_NOTES[i]);
                            }
                        }
                    }
                }
            }
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void info(String message) { LOGGER.info(message); }
    public static void warn(String message) {
        LOGGER.warn(message);
    }
}