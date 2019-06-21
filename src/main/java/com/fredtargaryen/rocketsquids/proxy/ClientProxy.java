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
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.Iterator;

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
    public BipedModel getConchModel() {
        return new ConchModel();
    }

    @Override
    public void playNoteFromMessage(byte note) {
        PlayerEntity ep = Minecraft.getInstance().player;
        ep.world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[note], SoundCategory.PLAYERS, 1.0F, 1.0F, true);
    }

    @Override
    public void playNoteFromMessageConchNeeded(byte note) {
        PlayerEntity ep = Minecraft.getInstance().player;
        //Check player is wearing the conch
        Iterable<ItemStack> armour = ep.getArmorInventoryList();
        Iterator<ItemStack> iter = armour.iterator();
        iter.next();
        iter.next();
        iter.next();
        ItemStack helmet = iter.next();
        if(helmet.getItem() == RocketSquidsBase.ITEM_CONCH) {
            ep.world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[note], SoundCategory.NEUTRAL, 1.0F, 1.0F, true);
        }
    }
}