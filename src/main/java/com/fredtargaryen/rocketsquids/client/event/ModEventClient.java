package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquid;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquidBaby;
import com.fredtargaryen.rocketsquids.client.render.RenderBabyRS;
import com.fredtargaryen.rocketsquids.client.render.RenderRS;
import com.fredtargaryen.rocketsquids.content.ModBlocks;
import com.fredtargaryen.rocketsquids.content.ModEntities;
import com.fredtargaryen.rocketsquids.content.ModItems;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClient {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::registerRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEventClient::registerLayerDefinitions);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // register block renderers
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_CONCH.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_STATUE.get(), RenderType.cutoutMipped());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // register normal entity renderers
        event.registerEntityRenderer(ModEntities.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // register custom entity renderers
        event.registerEntityRenderer(ModEntities.SQUID_TYPE.get(), RenderRS::new);
        event.registerEntityRenderer(ModEntities.BABY_SQUID_TYPE.get(), RenderBabyRS::new);
    }

    public static final ModelLayerLocation SQUID_BODY_LAYER;

    static {
        assert ModEntities.SQUID_TYPE.getId() != null;
        SQUID_BODY_LAYER = new ModelLayerLocation(ModEntities.SQUID_TYPE.getId(), "body");
    }

    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        assert ModEntities.BABY_SQUID_TYPE.getId() != null;
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(ModEntities.BABY_SQUID_TYPE.getId(), "body");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SQUID_BODY_LAYER, ModelRocketSquid::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, ModelRocketSquidBaby::createBodyLayer);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessage(byte note) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level().playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.PLAYERS, 1.0F, 1.0F, true);
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
        if (helmet.getItem() == ModItems.ITEM_CONCH.get()) {
            Vec3 pos = player.position();
            player.level().playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }
}