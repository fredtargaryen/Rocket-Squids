package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Iterator;

public class MessageSquidNote implements IMessage, IMessageHandler<MessageSquidNote, IMessage> {
    private byte note;

    public MessageSquidNote() {}

    public MessageSquidNote(byte note) {
        this.note = note;
    }

    public IMessage onMessage(final MessageSquidNote message, MessageContext ctx) {
        final IThreadListener clientListener = Minecraft.getMinecraft();
        clientListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                Minecraft mc = (Minecraft) clientListener;
                EntityPlayer ep = mc.player;
                //Check player is wearing the conch
                Iterable<ItemStack> armour = ep.getArmorInventoryList();
                Iterator<ItemStack> iter = armour.iterator();
                iter.next();
                iter.next();
                iter.next();
                ItemStack helmet = iter.next();
                if(helmet.getItem() == RocketSquidsBase.itemConch) {
                    mc.world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[message.note], SoundCategory.NEUTRAL, 1.0F, 1.0F, true);
                }
            }
        });
        return null;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.note = buf.readByte();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.note);
    }
}
