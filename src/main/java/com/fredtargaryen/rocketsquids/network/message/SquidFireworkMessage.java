// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Creates the exploded rocket squid firework effect.
 * Direction: server to client
 *
 * @param uuid The UUID of the squid that exploded
 */
public record SquidFireworkMessage(UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SquidFireworkMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("firework"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SquidFireworkMessage> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, SquidFireworkMessage::uuid,
                    SquidFireworkMessage::new);

    public static void handle(final SquidFireworkMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleMessage(message));
    }
}
