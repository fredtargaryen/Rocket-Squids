// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Syncs the capability for baby rocket squids.
 * Direction: server to client
 *
 * @param uuid The UUID of the baby rocket squid
 * @param data The capability data to send to the client
 */
public record BabyCapDataMessage(UUID uuid, CompoundTag data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BabyCapDataMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("baby_data"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BabyCapDataMessage> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, BabyCapDataMessage::uuid,
                    ByteBufCodecs.COMPOUND_TAG, BabyCapDataMessage::data,
                    BabyCapDataMessage::new);

    public static void handle(final BabyCapDataMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handleMessage(message));
    }
}
