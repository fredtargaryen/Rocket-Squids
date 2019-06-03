package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlayNoteClient {
    private byte note;

    public MessagePlayNoteClient() {}

    public MessagePlayNoteClient(byte note) {
        this.note = note;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
                EntityPlayer ep = ctx.get().getSender();
                ep.world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[this.note], SoundCategory.PLAYERS, 1.0F, 1.0F, true);
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessagePlayNoteClient(ByteBuf buf)
    {
        this.note = buf.readByte();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.note);
    }
}
