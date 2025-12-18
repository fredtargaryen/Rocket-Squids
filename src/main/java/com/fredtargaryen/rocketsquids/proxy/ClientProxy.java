package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.client.render.RenderBabyRS;
import com.fredtargaryen.rocketsquids.client.render.RenderRS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static com.fredtargaryen.rocketsquids.RocketSquidsBase.BABY_SQUID_TYPE;
import static com.fredtargaryen.rocketsquids.RocketSquidsBase.SQUID_TYPE;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientProxy implements IProxy {
    public static final ModelLayerLocation SQUID_BODY_LAYER = new ModelLayerLocation(SQUID_TYPE.getId(), "body");
    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER = new ModelLayerLocation(BABY_SQUID_TYPE.getId(), "body");

    @Override
    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SQUID_TYPE.get(), RenderRS::new);
        event.registerEntityRenderer(BABY_SQUID_TYPE.get(), RenderBabyRS::new);
        event.registerEntityRenderer(RocketSquidsBase.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RocketSquidsBase.TUBE_TYPE.get(), ThrownItemRenderer::new);
    }

    @Override
    @SubscribeEvent
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(SQUID_BODY_LAYER, ModelRocketSquid::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, ModelRocketSquidBaby::createBodyLayer);
    }

    @Override
    public void registerRenderTypes() {
        ItemBlockRenderTypes.setRenderLayer(RocketSquidsBase.BLOCK_CONCH.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(RocketSquidsBase.BLOCK_STATUE.get(), RenderType.cutoutMipped());
    }

    @Override
    public void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    @Override
    public HumanoidModel<?> getConchModel() {
        return new ModelConchArmor(1.0f);
    }

    @Override
    public void playNoteFromMessage(byte note) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level.playLocalSound(pos.x, pos.y, pos.z, Sounds.CONCH_NOTES[note], SoundSource.PLAYERS, 1.0F, 1.0F, true);
    }

    @Override
    public void playNoteFromMessageConchNeeded(byte note) {
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
            ep.level.playLocalSound(pos.x, pos.y, pos.z, Sounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }
}