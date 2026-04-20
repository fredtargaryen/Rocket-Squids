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
 * Play a note from the client, but the note is sung by a squid, so it should only be heard by a client player if
 * they're close enough and wearing the conch.
 * Direction: server to client
 *
 * @param note The index of the note to play
 */
public record SquidNoteMessage(byte note) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SquidNoteMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("note_client_squid"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SquidNoteMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, SquidNoteMessage::note,
                    SquidNoteMessage::new);

    public static void handle(final SquidNoteMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleMessage(message));
    }
}
