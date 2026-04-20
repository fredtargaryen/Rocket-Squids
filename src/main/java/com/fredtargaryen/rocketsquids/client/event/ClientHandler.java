// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.RSEntityTypes;
import com.fredtargaryen.rocketsquids.RSItems;
import com.fredtargaryen.rocketsquids.RSParticleTypes;
import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.BabyRocketSquidModel;
import com.fredtargaryen.rocketsquids.client.model.RocketSquidModel;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.client.render.BabyRocketSquidRenderer;
import com.fredtargaryen.rocketsquids.client.render.RocketSquidRenderer;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static com.fredtargaryen.rocketsquids.RSAttachmentTypes.SQUID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register block renderers
        //ItemBlockRenderTypes.setRenderLayer(RSBlocks.CONCH.get(), RenderType.cutoutMipped());
        //ItemBlockRenderTypes.setRenderLayer(RSBlocks.STATUE.get(), RenderType.cutoutMipped());

        // Register normal entity renderers
        event.registerEntityRenderer(RSEntityTypes.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RSEntityTypes.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // Register custom entity renderers
        event.registerEntityRenderer(RSEntityTypes.SQUID_TYPE.get(), RocketSquidRenderer::new);
        event.registerEntityRenderer(RSEntityTypes.BABY_SQUID_TYPE.get(), BabyRocketSquidRenderer::new);
    }

    public static final ModelLayerLocation SQUID_BODY_LAYER;

    static {
        assert RSEntityTypes.SQUID_TYPE.getId() != null;
        SQUID_BODY_LAYER = new ModelLayerLocation(RSEntityTypes.SQUID_TYPE.getId(), "body");
    }

    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        assert RSEntityTypes.BABY_SQUID_TYPE.getId() != null;
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(RSEntityTypes.BABY_SQUID_TYPE.getId(), "body");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SQUID_BODY_LAYER, RocketSquidModel::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, BabyRocketSquidModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RSParticleTypes.FIREWORK_TYPE.get(), SquidFireworkParticle.SparkFactory::new);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessageConchNeeded(byte note) {
        Player player = Minecraft.getInstance().player;
        // Check if the player is wearing the conch
        assert player != null;
        Iterable<ItemStack> armour = player.getArmorSlots();
        Iterator<ItemStack> iter = armour.iterator();
        iter.next();
        iter.next();
        iter.next();
        ItemStack helmet = iter.next();
        if (helmet.getItem() == RSItems.ITEM_CONCH.get()) {
            Vec3 pos = player.position();
            player.level().playLocalSound(pos.x, pos.y, pos.z, RSSounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }

    public static void handleMessage(AdultCapDataMessage message) {
        if (Minecraft.getInstance().level == null) return;
        Iterable<Entity> l = Minecraft.getInstance().level.entitiesForRendering();
        Iterator<Entity> squidFinder = l.iterator();
        Entity e;
        while (squidFinder.hasNext()) {
            e = squidFinder.next();
            if (e.getUUID().equals(message.uuid())) {
                e.getData(SQUID).deserializeNBT(null, message.data());
            }
        }
    }

    public static void handleMessage(BabyCapDataMessage message) {
        if (Minecraft.getInstance().level == null) return;
        Iterable<Entity> l = Minecraft.getInstance().level.entitiesForRendering();
        Iterator<Entity> squidFinder = l.iterator();
        Entity e;
        while (squidFinder.hasNext()) {
            e = squidFinder.next();
            if (e.getUUID().equals(message.uuid())) {
                e.getData(SQUID).deserializeNBT(null, message.data());
            }
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