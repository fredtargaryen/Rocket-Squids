// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.config.Config;
import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.fredtargaryen.rocketsquids.level.capability.entity.adult.AdultCap;
import com.fredtargaryen.rocketsquids.level.capability.entity.adult.AdultCapProvider;
import com.fredtargaryen.rocketsquids.level.capability.entity.baby.BabyCap;
import com.fredtargaryen.rocketsquids.level.capability.entity.baby.BabyCapProvider;
import com.fredtargaryen.rocketsquids.level.capability.item.squeleporter.SqueleporterCap;
import com.fredtargaryen.rocketsquids.level.capability.item.squeleporter.SqueleporterCapProvider;
import com.fredtargaryen.rocketsquids.level.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
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

    public static MobSpawnSettings.SpawnerData ROCKET_SQUID_SPAWN_INFO;

    /**
     * A custom firework that looks kinda like a Rocket Squid, created in {@link RocketSquidsBase#setupFirework()}
     * Firework structure:
     * TagCompound          (firework)
     * |_TagList            (list, "Explosions")
     * |_TagCompound      (Single firework part)
     * |_TagBoolean     ("Trail")
     * |_TagBoolean     ("Flicker")
     * |_TagIntArray    ("Colors")
     * |_TagIntArray    ("FadeColors")
     */
    public static final CompoundTag firework = new CompoundTag();

    /**
     * Set up the tag describing the rocket squid firework
     */
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

        final IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        RSBlocks.register(modEventBus);
        RSItems.register(modEventBus);
        // Also populates the creative tab
        RSCreativeTabs.register(modEventBus);
        // Also registers the spawn egg
        RSEntities.register(modEventBus);
        // Register our world gen features
        RSFeatures.register(modEventBus);
        RSParticleTypes.register(modEventBus);
        RSSounds.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));

        IEventBus loadingBus = context.getModEventBus();
        loadingBus.addListener(this::postRegistration);
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     *
     * @param event FMLCommonSetupEvent
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        MessageHandler.init();

        setupFirework();

        // Validate the config
        if (GeneralConfig.MAX_GROUP_SIZE.get() < GeneralConfig.MIN_GROUP_SIZE.get()) {
            GeneralConfig.MAX_GROUP_SIZE = GeneralConfig.MIN_GROUP_SIZE;
        }

        // Spawn info (might be redundant due to biomemodifiers)
        // noinspection deprecation
        SpawnPlacements.register(RSEntities.SQUID_TYPE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> true);
        ROCKET_SQUID_SPAWN_INFO = new MobSpawnSettings.SpawnerData(RSEntities.SQUID_TYPE.get(), GeneralConfig.SPAWN_PROB.get(), GeneralConfig.MIN_GROUP_SIZE.get(), GeneralConfig.MAX_GROUP_SIZE.get());
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
        if (evt.getObject().getItem() == RSItems.SQUELEPORTER_ACTIVE.get()) {
            evt.addCapability(DataReference.SQUELEPORTER_LOCATION, new SqueleporterCapProvider());
        }
    }
}