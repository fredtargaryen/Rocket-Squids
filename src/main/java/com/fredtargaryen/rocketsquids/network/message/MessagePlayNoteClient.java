package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayNoteClient implements IMessage, IMessageHandler<MessagePlayNoteClient, IMessage> {
    private byte note;

    public MessagePlayNoteClient() {}

    public MessagePlayNoteClient(byte note) {
        this.note = note;
    }

    public IMessage onMessage(final MessagePlayNoteClient message, MessageContext ctx) {
        final IThreadListener clientListener = Minecraft.getMinecraft();
        clientListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer ep = ((Minecraft) clientListener).player;
                ((Minecraft) clientListener).world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[message.note], SoundCategory.PLAYERS, 1.0F, 1.0F, true);
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
