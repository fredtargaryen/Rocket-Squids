// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.*;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.BabyRocketSquidModel;
import com.fredtargaryen.rocketsquids.client.model.RocketSquidModel;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.client.render.RocketSquidRenderer;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientHandler {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register normal entity renderers
        event.registerEntityRenderer(RSEntityTypes.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RSEntityTypes.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // Register custom entity renderers
        event.registerEntityRenderer(RSEntityTypes.SQUID_TYPE.get(), RocketSquidRenderer::new);
    }

    public static final ContextKey<UUID> PLAYER_ID = new ContextKey<>(DataReference.getIdentifier("player_id"));

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                renderState.setRenderData(PLAYER_ID, avatar.getUUID());
            }
        });
    }

    /**
     * Unused provider passed to certain methods so that they don't complain
     */
    private static final HolderLookup.Provider dummyLookupProvider;

    public static final ModelLayerLocation SQUID_BODY_LAYER;
    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        SQUID_BODY_LAYER = new ModelLayerLocation(RSEntityTypes.SQUID_TYPE.getId(), "body");
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(RSEntityTypes.SQUID_TYPE.getId(), "body_baby");
        dummyLookupProvider = HolderLookup.Provider.create(Stream.empty());
    }

    @SubscribeEvent
    public static void populateCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(RSItems.ROCKET_SQUID_SPAWN_EGG.get());
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SQUID_BODY_LAYER, RocketSquidModel::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, BabyRocketSquidModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RSParticleTypes.FIREWORK_TYPE.get(), SquidFireworkParticle.SparkProvider::new);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessageConchNeeded(int note) {
        Player player = Minecraft.getInstance().player;
        // Check if the player is wearing the conch
        assert player != null;
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == RSItems.ITEM_CONCH.get()) {
            Vec3 pos = player.position();
            player.level().playLocalSound(pos.x, pos.y, pos.z, RSSounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }

    public static void handleMessage(PlayNoteClientMessage message) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level().playLocalSound(pos.x, pos.y, pos.z, RSSounds.CONCH_NOTES[message.note()], SoundSource.PLAYERS, 1.0F, 1.0F, true);
    }

    public static void handleMessage(SquidFireworkMessage message) {
        assert Minecraft.getInstance().level != null;
        Iterable<Entity> l = Minecraft.getInstance().level.entitiesForRendering();
        Iterator<Entity> squidFinder = l.iterator();
        Entity entity;
        while (squidFinder.hasNext()) {
            entity = squidFinder.next();
            if (entity.getUUID().equals(message.uuid())) {
                RocketSquidEntity rocketSquidEntity = (RocketSquidEntity) entity;
                rocketSquidEntity.doFireworkParticles();
                break;
            }
        }
    }

    public static void handleMessage(SquidNoteMessage message) {
        ClientHandler.playNoteFromMessageConchNeeded(message.note());
    }
}