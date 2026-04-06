// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.*;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquid;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquidBaby;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.client.render.RenderBabyRS;
import com.fredtargaryen.rocketsquids.client.render.RenderRS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register block renderers
        ItemBlockRenderTypes.setRenderLayer(RSBlocks.BLOCK_CONCH.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(RSBlocks.BLOCK_STATUE.get(), RenderType.cutoutMipped());

        // Register normal entity renderers
        event.registerEntityRenderer(RSEntities.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RSEntities.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // Register custom entity renderers
        event.registerEntityRenderer(RSEntities.SQUID_TYPE.get(), RenderRS::new);
        event.registerEntityRenderer(RSEntities.BABY_SQUID_TYPE.get(), RenderBabyRS::new);
    }

    public static final ModelLayerLocation SQUID_BODY_LAYER;

    static {
        assert RSEntities.SQUID_TYPE.getId() != null;
        SQUID_BODY_LAYER = new ModelLayerLocation(RSEntities.SQUID_TYPE.getId(), "body");
    }

    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        assert RSEntities.BABY_SQUID_TYPE.getId() != null;
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(RSEntities.BABY_SQUID_TYPE.getId(), "body");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SQUID_BODY_LAYER, ModelRocketSquid::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, ModelRocketSquidBaby::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RSParticleTypes.FIREWORK_TYPE.get(), SquidFireworkParticle.SparkFactory::new);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessage(byte note) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level().playLocalSound(pos.x, pos.y, pos.z, RSSounds.CONCH_NOTES[note], SoundSource.PLAYERS, 1.0F, 1.0F, true);
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
}