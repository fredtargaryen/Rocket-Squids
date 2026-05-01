// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Plays a note on the client.
 * Direction: server to client
 *
 * @param note The note to play
 */
public record PlayNoteClientMessage(byte note) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayNoteClientMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("note_client"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PlayNoteClientMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, PlayNoteClientMessage::note,
                    PlayNoteClientMessage::new);

    public static void handle(final PlayNoteClientMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleMessage(message));
    }
}
