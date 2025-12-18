package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.block.ConchBlock;
import com.fredtargaryen.rocketsquids.block.StatueBlock;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.config.Config;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.capability.adult.IAdultCapability;
import com.fredtargaryen.rocketsquids.entity.capability.baby.IBabyCapability;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.item.*;
import com.fredtargaryen.rocketsquids.item.capability.ISqueleporter;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.proxy.ClientProxy;
import com.fredtargaryen.rocketsquids.proxy.IProxy;
import com.fredtargaryen.rocketsquids.proxy.ServerProxy;
import com.fredtargaryen.rocketsquids.worldgen.*;
import com.fredtargaryen.rocketsquids.util.ColorHelper;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@Mod(value = MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RocketSquidsBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The creative tab for all items from Rocket Squids.
     */
    public static CreativeModeTab SQUIDS_TAB = new CreativeModeTab(MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return ITEM_CONCH.get().getDefaultInstance();
        }
    };

    // Blocks
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Register all blocks here
    public static final RegistryObject<ConchBlock> BLOCK_CONCH = BLOCKS.register("conch", () -> new ConchBlock(Block.Properties.of(Material.SAND).noCollission()));
    public static final RegistryObject<StatueBlock> BLOCK_STATUE = BLOCKS.register("statue", () -> new StatueBlock(Block.Properties.of(Material.STONE).noOcclusion()));

    // Items
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Register all items here
    public static final RegistryObject<Item> ITEM_CONCH = ITEMS.register("conch_item_1", () -> new ItemConch(new Item.Properties().tab(RocketSquidsBase.SQUIDS_TAB).stacksTo(4)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH2 = ITEMS.register("conch_item_2", () -> new ItemConch2(new Item.Properties().tab(RocketSquidsBase.SQUIDS_TAB).stacksTo(1).rarity(Rarity.UNCOMMON)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH3 = ITEMS.register("conch_item_3", () -> new ItemConch3(new Item.Properties().tab(RocketSquidsBase.SQUIDS_TAB).stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NITRO_SAC = ITEMS.register("nitro_ink_sac", () -> new ItemNitroInkSac(new Item.Properties().tab(RocketSquidsBase.SQUIDS_TAB).stacksTo(16)));
    public static final RegistryObject<Item> TURBO_TUBE = ITEMS.register("turbo_tube", () -> new ItemTurboTube(new Item.Properties().tab(SQUIDS_TAB).stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ITEM_STATUE = ITEMS.register("statue", () -> new BlockItem(BLOCK_STATUE.get(), new Item.Properties().tab(RocketSquidsBase.SQUIDS_TAB).stacksTo(4).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SQUAVIGATOR = ITEMS.register("squavigator", () -> new Item(new Item.Properties().tab(SQUIDS_TAB).stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SQUELEPORTER_ACTIVE = ITEMS.register("squeleporter_active", () -> new ItemSqueleporter(new Item.Properties().tab(SQUIDS_TAB).stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SQUELEPORTER_INACTIVE = ITEMS.register("squeleporter_inactive", () -> new ItemSqueleporter(new Item.Properties().tab(SQUIDS_TAB).stacksTo(1).rarity(Rarity.UNCOMMON)));

    // Entities
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    // Register all EntityTypes here
    public static final RegistryObject<EntityType<ThrownSacEntity>> SAC_TYPE = ENTITIES.register("thrown_nitro_ink_sac",
            () -> EntityType.Builder.<ThrownSacEntity>of(ThrownSacEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    public static final RegistryObject<EntityType<ThrownTubeEntity>> TUBE_TYPE = ENTITIES.register("turbo_tube",
            () -> EntityType.Builder.<ThrownTubeEntity>of(ThrownTubeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .setCustomClientFactory(ThrownTubeEntity::new)
                    .build(MODID)
    );
    public static final RegistryObject<EntityType<RocketSquidEntity>> SQUID_TYPE = ENTITIES.register("rocket_squid",
            () -> EntityType.Builder.<RocketSquidEntity>of((type, world) -> new RocketSquidEntity(world), MobCategory.WATER_CREATURE)
                    .sized(0.99F, 0.99F)
                    .setTrackingRange(128)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );
    public static final RegistryObject<EntityType<BabyRocketSquidEntity>> BABY_SQUID_TYPE = ENTITIES.register("baby_rocket_squid",
            () -> EntityType.Builder.<BabyRocketSquidEntity>of(BabyRocketSquidEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );

    // Spawn Egg Items
    private static final DeferredRegister<Item> SPAWNEGGITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Register Spawn Egg Items here
    @SuppressWarnings("unused")
    public static final RegistryObject<RocketSquidForgeSpawnEggItem> SQUID_SPAWN_EGG = SPAWNEGGITEMS.register("rockets_squid_spawn_egg",
            () -> new RocketSquidForgeSpawnEggItem(SQUID_TYPE, BABY_SQUID_TYPE, ColorHelper.getColor(150, 30, 30), ColorHelper.getColor(255, 127, 0), new Item.Properties().tab(SQUIDS_TAB))
    ); // Hey if you wanted to know do not use SpawnEggItem use ForgeSpawnEggItem

    // Particles
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    // Register all ParticleTypes here
    public static final RegistryObject<ParticleType<SimpleParticleType>> FIREWORK_TYPE = PARTICLE_TYPES.register("firework",
            () -> new SimpleParticleType(false));

    // WorldGen Features
    private static final DeferredRegister<Feature<?>> WORLDGEN_FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    // Register all Features here
    public static final RegistryObject<Feature<ConchGenConfig>> CONCH_FEATURE = WORLDGEN_FEATURES.register("conchgen",
            () -> new ConchGen(ConchGenConfig.FACTORY)
    );
    public static final RegistryObject<Feature<StatueGenConfig>> STATUE_FEATURE = WORLDGEN_FEATURES.register("statuegen",
            () -> new StatueGen(StatueGenConfig.FACTORY)
    );

    // WorldGen Decorators
    private static final DeferredRegister<FeatureDecorator<?>> WORLDGEN_DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, MODID);
    // Register all Decorators here
    public static final RegistryObject<ConchPlacement> CONCH_PLACEMENT = WORLDGEN_DECORATORS.register("conchplace",
            () -> new ConchPlacement(NoneDecoratorConfiguration.CODEC)
    );
    public static final RegistryObject<StatuePlacement> STATUE_PLACEMENT = WORLDGEN_DECORATORS.register("statueplace",
            () -> new StatuePlacement(NoneDecoratorConfiguration.CODEC)
    );

    public static FeatureManager FEATURE_MANAGER;

    public static MobSpawnSettings.SpawnerData ROCKET_SQUID_SPAWN_INFO;


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
    public static final CompoundTag firework = new CompoundTag();

    /**
     * Says where the client and server 'proxy' code is loaded.
     */
    public static IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public RocketSquidsBase() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);

        // Register DeferredRegister stuff
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        SPAWNEGGITEMS.register(modEventBus);

        PARTICLE_TYPES.register(modEventBus);

        WORLDGEN_FEATURES.register(modEventBus);
        WORLDGEN_DECORATORS.register(modEventBus);

        // Event bus
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);
        //loadingBus.addListener(this::clientSetup);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register and load the config
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    }

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(FIREWORK_TYPE.get(), SquidFireworkParticle.SparkFactory::new);
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        FEATURE_MANAGER = new FeatureManager();
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        Sounds.constructAndRegisterSoundEvents(event);
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event FMLCommonSetupEvent
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        MessageHandler.init();

        //Add entity attributes
        event.enqueueWork(() -> DefaultAttributes.put(RocketSquidsBase.BABY_SQUID_TYPE.get(), BabyRocketSquidEntity.createAttributes().build()));
        event.enqueueWork(() -> DefaultAttributes.put(RocketSquidsBase.SQUID_TYPE.get(), RocketSquidEntity.createAttributes().build()));

        //Make the firework
        ListTag list = new ListTag();
        CompoundTag f1 = new CompoundTag();
            f1.putBoolean("Flicker", false);
            f1.putBoolean("Trail", false);
            f1.putIntArray("Colors", new int[]{15435844});
            f1.putIntArray("FadeColors", new int[]{6719955});
        list.add(f1);
        firework.put("Explosions", list);

        //Validate the config
        if(GeneralConfig.MAX_GROUP_SIZE.get() < GeneralConfig.MIN_GROUP_SIZE.get()) {
            GeneralConfig.MAX_GROUP_SIZE = GeneralConfig.MIN_GROUP_SIZE;
        }

        //Spawn info
        SpawnPlacements.register(SQUID_TYPE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> true);
        ROCKET_SQUID_SPAWN_INFO = new MobSpawnSettings.SpawnerData(SQUID_TYPE.get(), GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get());
    }

    /////////////////
    //CAPABILIITIES//
    /////////////////
    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IBabyCapability.class);
        event.register(IAdultCapability.class);
        event.register(ISqueleporter.class);
    }

    public static final Capability<IBabyCapability> BABYCAP = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAdultCapability> ADULTCAP = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ISqueleporter> SQUELEPORTER_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public void onItemStackConstruct(AttachCapabilitiesEvent<ItemStack> evt) {
        if(evt.getObject().getItem() == SQUELEPORTER_ACTIVE.get()) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION,
                    new ICapabilitySerializable<CompoundTag>() {
                        final ISqueleporter inst = SQUELEPORTER_CAP.getDefaultInstance();


                        @Override
                        public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
                            return capability == SQUELEPORTER_CAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                        }

                        @Override
                        public CompoundTag serializeNBT() {
                            return (CompoundTag) SQUELEPORTER_CAP.getStorage().writeNBT(SQUELEPORTER_CAP, inst, null);
                        }

                        @Override
                        public void deserializeNBT(CompoundTag nbt) {
                            SQUELEPORTER_CAP.getStorage().readNBT(SQUELEPORTER_CAP, inst, null, nbt);
                        }
                    });
        }
    }

    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent<Entity> evt) {
        Entity e = evt.getObject();
        if(e instanceof BabyRocketSquidEntity) {
            evt.addCapability(DataReference.BABY_CAP_LOCATION,
                    //Full name ICapabilitySerializableProvider
                    new ICapabilitySerializable<CompoundTag>() {
                        final IBabyCapability inst = BABYCAP.getDefaultInstance();

                        @Override
                        public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
                            return capability == BABYCAP ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
                        }

                        @Override
                        public CompoundTag serializeNBT() {
                            return (CompoundTag) BABYCAP.getStorage().writeNBT(BABYCAP, inst, null);
                        }

                        @Override
                        public void deserializeNBT(CompoundTag nbt) {
                            BABYCAP.getStorage().readNBT(BABYCAP, inst, null, nbt);
                        }
                    });
        }
        else if(e instanceof RocketSquidEntity) {
            evt.addCapability(DataReference.ADULT_CAP_LOCATION,
                    //Full name ICapabilitySerializableProvider
                    new ICapabilitySerializable<CompoundTag>() {
                        final IAdultCapability inst = ADULTCAP.getDefaultInstance();

                        @Override
                        public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
                            return capability == ADULTCAP ? LazyOptional.of(() -> (T)inst) : LazyOptional.empty();
                        }

                        @Override
                        public CompoundTag serializeNBT() {
                            return (CompoundTag) ADULTCAP.getStorage().writeNBT(ADULTCAP, inst, null);
                        }

                        @Override
                        public void deserializeNBT(CompoundTag nbt) {
                            ADULTCAP.getStorage().readNBT(ADULTCAP, inst, null, nbt);
                        }
                    }
            );
        }
    }

    public static Vector3f getPlayerAimVector(Player player)
    {
        double rp = Math.toRadians(player.getXRot());
        double ry = Math.toRadians(player.getYRot());
        float y = (float) -Math.sin(rp);
        float hori = (float) Math.cos(rp);
        float x = (float) (hori * -Math.sin(ry));
        float z = (float) (hori * Math.cos(ry));
        return new Vector3f(x, y, z);
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void info(String message) { LOGGER.info(message); }
    public static void warn(String message) { LOGGER.warn(message); }
}