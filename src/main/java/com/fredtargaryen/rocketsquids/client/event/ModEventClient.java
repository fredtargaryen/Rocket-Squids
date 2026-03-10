package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.client.render.RenderBabyRS;
import com.fredtargaryen.rocketsquids.client.render.RenderRS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static com.fredtargaryen.rocketsquids.RocketSquidsBase.*;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClient {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::registerRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::registerLayerDefinitions);
    }

    public static final ModelLayerLocation SQUID_BODY_LAYER;

    static {
        assert SQUID_TYPE.getId() != null;
        SQUID_BODY_LAYER = new ModelLayerLocation(SQUID_TYPE.getId(), "body");
    }

    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        assert BABY_SQUID_TYPE.getId() != null;
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(BABY_SQUID_TYPE.getId(), "body");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(SQUID_BODY_LAYER, ModelRocketSquid::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, ModelRocketSquidBaby::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // register block renderers
        ItemBlockRenderTypes.setRenderLayer(RocketSquidsBase.BLOCK_CONCH.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(RocketSquidsBase.BLOCK_STATUE.get(), RenderType.cutoutMipped());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // register normal entity renderers
        event.registerEntityRenderer(RocketSquidsBase.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RocketSquidsBase.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // register custom entity renderers
        event.registerEntityRenderer(SQUID_TYPE.get(), RenderRS::new);
        event.registerEntityRenderer(BABY_SQUID_TYPE.get(), RenderBabyRS::new);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessage(byte note) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level.playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.PLAYERS, 1.0F, 1.0F, true);
    }

    public static void playNoteFromMessageConchNeeded(byte note) {
        Player ep = Minecraft.getInstance().player;
        //Check player is wearing the conch
        assert ep != null;
        Iterable<ItemStack> armour = ep.getArmorSlots();
        Iterator<ItemStack> iter = armour.iterator();
        iter.next();
        iter.next();
        iter.next();
        ItemStack helmet = iter.next();
        if(helmet.getItem() == RocketSquidsBase.ITEM_CONCH.get()) {
            Vec3 pos = ep.position();
            ep.level.playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }
}