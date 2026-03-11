package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.client.event.ModClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlayNoteClient {
    private byte note;

    @SuppressWarnings("unused")
    public MessagePlayNoteClient() {

    }

    public MessagePlayNoteClient(byte note) {
        this.note = note;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ModClientHandler.playNoteFromMessage(this.note));
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
