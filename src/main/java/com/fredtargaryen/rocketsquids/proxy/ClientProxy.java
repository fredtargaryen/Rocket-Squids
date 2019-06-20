package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownSacEntity;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownTubeEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(RocketSquidEntity.class, new RenderRSFactory());
        RenderingRegistry.registerEntityRenderingHandler(ThrownSacEntity.class, manager -> new SpriteRenderer<ThrownSacEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ThrownTubeEntity.class, manager -> new SpriteRenderer<ThrownTubeEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(BabyRocketSquidEntity.class, new RenderBabyRSFactory());
    }

    @Override
    public void openConchClient(byte conchStage) {
        Minecraft.getInstance().displayGuiScreen(new ConchScreen(conchStage));
    }

    @Override
    public void playNote(byte note) {
        PlayerEntity ep = Minecraft.getInstance().player;
        MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer(note, ep.posX, ep.posY, ep.posZ));
    }

    @Override
    public BipedModel getConchModel() {
        ConchModel mc = new ConchModel();
        mc.register();
        return mc;
    }
}