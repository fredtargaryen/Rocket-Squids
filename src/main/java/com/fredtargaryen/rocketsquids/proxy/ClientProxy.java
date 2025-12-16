package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.ConchModel;
import com.fredtargaryen.rocketsquids.client.model.RenderBabyRSFactory;
import com.fredtargaryen.rocketsquids.client.model.RenderRSFactory;
import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(RocketSquidsBase.SQUID_TYPE.get(), new RenderRSFactory());
        RenderingRegistry.registerEntityRenderingHandler(RocketSquidsBase.SAC_TYPE.get(), manager -> new ThrownItemRenderer<ThrownSacEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(RocketSquidsBase.TUBE_TYPE.get(), manager -> new ThrownItemRenderer<ThrownTubeEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(RocketSquidsBase.BABY_SQUID_TYPE.get(), new RenderBabyRSFactory());
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
    public HumanoidModel getConchModel() {
        return new ConchModel(1.0f);
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