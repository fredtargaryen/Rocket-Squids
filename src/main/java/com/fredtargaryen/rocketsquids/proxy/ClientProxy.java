package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.client.gui.GuiConch;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy
{
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRocketSquid.class, new RenderRSFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownSac.class, new RenderSacFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownTube.class, new RenderTubeFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityBabyRocketSquid.class, new RenderBabyRSFactory());
    }

    @Override
    public void openConchClient(byte conchStage) {
        Minecraft.getInstance().displayGuiScreen(new GuiConch(conchStage));
    }

    @Override
    public void playNote(byte note) {
        EntityPlayer ep = Minecraft.getInstance().player;
        MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer(note, ep.posX, ep.posY, ep.posZ));
    }

    @Override
    public ModelBiped getConchModel() {
        ModelConch mc = new ModelConch();
        mc.register();
        return mc;
    }
}