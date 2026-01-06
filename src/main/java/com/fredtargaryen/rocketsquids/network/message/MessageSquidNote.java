package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSquidNote {
    private byte note;

    @SuppressWarnings("unused")
    public MessageSquidNote() {

    }

    public MessageSquidNote(byte note) {
        this.note = note;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            RocketSquidsBase.proxy.playNoteFromMessageConchNeeded(this.note);
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageSquidNote(ByteBuf buf) {
        this.note = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.note);
    }
}
