// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Plays a note on the client.
 * Direction: server to client
 */
public class MessagePlayNoteClient {
    private byte note;

    @SuppressWarnings("unused")
    public MessagePlayNoteClient() {

    }

    public MessagePlayNoteClient(byte note) {
        this.note = note;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientHandler.playNoteFromMessage(this.note));
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
