// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Play a note from the client, but the note is sung by a squid, so it should only be heard by a client player if
 * they're close enough and wearing the conch.
 * Direction: server to client
 */
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
            ClientHandler.playNoteFromMessageConchNeeded(this.note);
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
