package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.client.gui.GuiConch;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRocketSquid.class, new RenderRSFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownSac.class, new RenderSacFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownTube.class, new RenderTubeFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityBabyRocketSquid.class, new RenderBabyRSFactory());
    }

    @Override
    public void registerModels() {
        //Describes how items and some blocks should look in the inventory
        ModelLoader.setCustomModelResourceLocation(RocketSquidsBase.itemConch, 0, new ModelResourceLocation(DataReference.MODID + ":conch", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RocketSquidsBase.itemConch2, 0, new ModelResourceLocation(DataReference.MODID + ":conchtwo", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RocketSquidsBase.itemConch3, 0, new ModelResourceLocation(DataReference.MODID + ":conchthree", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RocketSquidsBase.nitroinksac, 0, new ModelResourceLocation(DataReference.MODID + ":nitroinksac", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RocketSquidsBase.turbotube, 0, new ModelResourceLocation(DataReference.MODID + ":turbotube", "inventory"));
    }

    @Override
    public void openConchClient(byte conchStage) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiConch(conchStage));
    }

    @Override
    public void playNote(byte note) {
        EntityPlayer ep = Minecraft.getMinecraft().player;
        MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer(note, ep.posX, ep.posY, ep.posZ));
    }

    @Override
    public ModelBiped getConchModel() {
        ModelConch mc = new ModelConch();
        mc.register();
        return mc;
    }
}