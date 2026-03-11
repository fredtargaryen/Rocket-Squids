package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.client.event.ModEventClient;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.config.Config;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.content.*;
import com.fredtargaryen.rocketsquids.content.cap.entity.adult.AdultCap;
import com.fredtargaryen.rocketsquids.content.cap.entity.adult.AdultCapProvider;
import com.fredtargaryen.rocketsquids.content.cap.entity.baby.BabyCap;
import com.fredtargaryen.rocketsquids.content.cap.entity.baby.BabyCapProvider;
import com.fredtargaryen.rocketsquids.content.cap.item.squeleporter.SqueleporterCap;
import com.fredtargaryen.rocketsquids.content.cap.item.squeleporter.SqueleporterCapProvider;
import com.fredtargaryen.rocketsquids.content.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

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

    public static MobSpawnSettings.SpawnerData ROCKET_SQUID_SPAWN_INFO;

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

    public static void setupFirework() {
        ListTag list = new ListTag();
        CompoundTag f1 = new CompoundTag();
        f1.putBoolean("Flicker", false);
        f1.putBoolean("Trail", false);
        f1.putIntArray("Colors", new int[]{15435844});
        f1.putIntArray("FadeColors", new int[]{6719955});
        list.add(f1);

        firework.put("Explosions", list);
    }


    public RocketSquidsBase(FMLJavaModLoadingContext context) {
        INSTANCE = this;

        // Mod Event Bus
        final IEventBus modEventBus = context.getModEventBus();

        // Register ourselves on the event bus for various stuff
        MinecraftForge.EVENT_BUS.register(this);

        // Register our sounds
        ModSounds.register(modEventBus);

        // Register blocks and items
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        // Register entities + a spawn egg
        ModEntities.register(modEventBus);

        // Register our creative tab and put items in it
        ModCreativeTabs.register(modEventBus);

        // Register our particle along with it's factory
        PARTICLE_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerParticleFactories);

        // Register our world gen features
        ModFeatures.register(modEventBus);

        // Initilize our client side only stuff
        ModEventClient.init();

        // Register and load the mod config
        context.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));


        // Loading/Setup Event bus
        IEventBus loadingBus = context.getModEventBus();

        // Register our setup handler
        loadingBus.addListener(this::postRegistration);
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event FMLCommonSetupEvent
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        // initlize our custom packets
        MessageHandler.init();

        // Run the firework setup function
        setupFirework();

        // Validate the config
        if(GeneralConfig.MAX_GROUP_SIZE.get() < GeneralConfig.MIN_GROUP_SIZE.get()) {
            GeneralConfig.MAX_GROUP_SIZE = GeneralConfig.MIN_GROUP_SIZE;
        }

        // Spawn info (might be redundent due to biomemodifiers)
        // noinspection deprecation
        SpawnPlacements.register(ModEntities.SQUID_TYPE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> true);
        ROCKET_SQUID_SPAWN_INFO = new MobSpawnSettings.SpawnerData(ModEntities.SQUID_TYPE.get(), GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get());
    }

    ///////////////////
    ///CAPABILIITIES///
    ///////////////////
    public static final Capability<AdultCap> ADULTCAP = AdultCapProvider.ADULTCAP;
    public static final Capability<BabyCap> BABYCAP = BabyCapProvider.BABYCAP;

    public static final Capability<SqueleporterCap> SQUELEPORTER_CAP = SqueleporterCapProvider.SQUELEPORTER_CAP;

    @SubscribeEvent
    public void onRegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.register(AdultCap.class);
        event.register(BabyCap.class);
        event.register(SqueleporterCap.class);
    }

    @SubscribeEvent
    public void onEntityAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> evt) {
        Entity e = evt.getObject();
        if (e instanceof RocketSquidEntity) {
            evt.addCapability(DataReference.ADULT_CAP_LOCATION, new AdultCapProvider());
        } else if (e instanceof BabyRocketSquidEntity) {
            evt.addCapability(DataReference.BABY_CAP_LOCATION, new BabyCapProvider());
        }
    }

    @SubscribeEvent
    public void onItemAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> evt) {
        if(evt.getObject().getItem() == ModItems.SQUELEPORTER_ACTIVE.get()) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION, new SqueleporterCapProvider());
        }
    }
}