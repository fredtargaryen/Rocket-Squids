package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.content.block.ConchBlock;
import com.fredtargaryen.rocketsquids.content.block.StatueBlock;
import com.fredtargaryen.rocketsquids.content.cap.entity.adult.AdultCap;
import com.fredtargaryen.rocketsquids.content.cap.entity.adult.AdultCapProvider;
import com.fredtargaryen.rocketsquids.content.cap.entity.baby.BabyCap;
import com.fredtargaryen.rocketsquids.content.cap.entity.baby.BabyCapProvider;
import com.fredtargaryen.rocketsquids.content.cap.item.squeleporter.SqueleporterCap;
import com.fredtargaryen.rocketsquids.content.cap.item.squeleporter.SqueleporterCapProvider;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.config.Config;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.content.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.content.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.content.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.content.item.*;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.client.event.ModEventClient;
import com.fredtargaryen.rocketsquids.content.ModFeatures;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import org.joml.Vector3f;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@Mod(value = MODID)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RocketSquidsBase {
    // Get our logger
    public static final Logger LOGGER = LogManager.getLogger();

    private static RocketSquidsBase INSTANCE;
    public static RocketSquidsBase getInstance() {
        return INSTANCE;
    }

    // Blocks
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Register all blocks here
    public static final RegistryObject<ConchBlock> BLOCK_CONCH = BLOCKS.register("conch", () -> new ConchBlock(Block.Properties.of(Material.SAND).noCollission()));
    public static final RegistryObject<StatueBlock> BLOCK_STATUE = BLOCKS.register("statue", () -> new StatueBlock(Block.Properties.of(Material.STONE).noOcclusion()));


    // Items
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Register all items here
    public static final RegistryObject<Item> ITEM_CONCH = ITEMS.register("conch_item_1", () -> new ItemConch(new Item.Properties().stacksTo(4)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH2 = ITEMS.register("conch_item_2", () -> new ItemConch2(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> ITEM_CONCH3 = ITEMS.register("conch_item_3", () -> new ItemConch3(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NITRO_SAC = ITEMS.register("nitro_ink_sac", () -> new ItemNitroInkSac(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> TURBO_TUBE = ITEMS.register("turbo_tube", () -> new ItemTurboTube(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ITEM_STATUE = ITEMS.register("statue", () -> new BlockItem(BLOCK_STATUE.get(), new Item.Properties().stacksTo(4).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SQUAVIGATOR = ITEMS.register("squavigator", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SQUELEPORTER_ACTIVE = ITEMS.register("squeleporter_active", () -> new ItemSqueleporter(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SQUELEPORTER_INACTIVE = ITEMS.register("squeleporter_inactive", () -> new ItemSqueleporter(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));


    // Entities
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
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
    @SuppressWarnings("RedundantTypeArguments")
    public static final RegistryObject<EntityType<BabyRocketSquidEntity>> BABY_SQUID_TYPE = ENTITIES.register("baby_rocket_squid",
            () -> EntityType.Builder.<BabyRocketSquidEntity>of(BabyRocketSquidEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.4F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(MODID)
    );

    public static MobSpawnSettings.SpawnerData ROCKET_SQUID_SPAWN_INFO;

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // Entity attributes are stored in there class under the createAttributes() method not in the registry code
        event.put(RocketSquidsBase.SQUID_TYPE.get(), RocketSquidEntity.createAttributes().build());
        event.put(RocketSquidsBase.BABY_SQUID_TYPE.get(), BabyRocketSquidEntity.createAttributes().build());
    }


    // Spawn Egg Items
    private static final DeferredRegister<Item> SPAWNEGGITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Register Spawn Egg Items here
    @SuppressWarnings("unused")
    public static final RegistryObject<RocketSquidForgeSpawnEggItem> SQUID_SPAWN_EGG = SPAWNEGGITEMS.register("rockets_squid_spawn_egg",
            () -> new RocketSquidForgeSpawnEggItem(SQUID_TYPE, BABY_SQUID_TYPE, ColorHelper.getColor(150, 30, 30), ColorHelper.getColor(255, 127, 0), new Item.Properties())
    ); // Hey if you wanted to know do not use SpawnEggItem use ForgeSpawnEggItem


    /**
     * The creative tab for all items from Rocket Squids.
     */
    @SuppressWarnings("removal")
    public void buildContents(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(MODID, "rocket_squid_tab"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("item_group." + MODID + ".rocket_squid_tab"))
                        // Set icon of creative tab
                        .icon(() -> new ItemStack(ITEM_CONCH.get()))
                        // Add default items to tab
                        .displayItems((params, output) -> {
                            output.accept(ITEM_CONCH.get());
                            output.accept(ITEM_CONCH2.get());
                            output.accept(ITEM_CONCH3.get());
                            output.accept(NITRO_SAC.get());
                            output.accept(TURBO_TUBE.get());
                            output.accept(ITEM_STATUE.get());
                            output.accept(SQUAVIGATOR.get());
                            output.accept(SQUELEPORTER_INACTIVE.get());
                            output.accept(SQUID_SPAWN_EGG.get());
                        })
        );
    }


    // Particles
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    // Register all ParticleTypes here
    public static final RegistryObject<ParticleType<SimpleParticleType>> FIREWORK_TYPE = PARTICLE_TYPES.register("firework",
            () -> new SimpleParticleType(false));

    public void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(FIREWORK_TYPE.get(), SquidFireworkParticle.SparkFactory::new);
    }

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

    @SuppressWarnings("removal")
    public RocketSquidsBase() {
        INSTANCE = this;

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);

        ModSounds.register(modEventBus);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        ENTITIES.register(modEventBus);
        modEventBus.addListener(this::registerEntityAttributes);

        SPAWNEGGITEMS.register(modEventBus);

        modEventBus.addListener(this::buildContents);

        PARTICLE_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerParticleFactories);

        ModFeatures.register(modEventBus);

        // init ModEventClient
        ModEventClient.init();

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

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event FMLCommonSetupEvent
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        MessageHandler.init();

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

    ///////////////////
    ///CAPABILIITIES///
    ///////////////////
    @SubscribeEvent
    public void onRegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.register(AdultCap.class);
        event.register(BabyCap.class);
        event.register(SqueleporterCap.class);
    }

    public static final Capability<AdultCap> ADULTCAP = AdultCapProvider.ADULTCAP;
    public static final Capability<BabyCap> BABYCAP = BabyCapProvider.BABYCAP;
    public static final Capability<SqueleporterCap> SQUELEPORTER_CAP = SqueleporterCapProvider.SQUELEPORTER_CAP;

    @SubscribeEvent
    public void onEntityAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> evt) {
        Entity e = evt.getObject();
        if (e instanceof RocketSquidEntity) {
            evt.addCapability(DataReference.ADULT_CAP_LOCATION, new AdultCapProvider());
        }
        else if (e instanceof BabyRocketSquidEntity) {
            evt.addCapability(DataReference.BABY_CAP_LOCATION, new BabyCapProvider());
        }
    }

    @SubscribeEvent
    public void onItemAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> evt) {
        if(evt.getObject().getItem() == SQUELEPORTER_ACTIVE.get()) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION, new SqueleporterCapProvider());
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
}