// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.TrickParameters;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FriendlyByteBufUtil;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Makes the squid the player is riding do a trick.
 * Direction: client to server
 *
 * @param trickParams the parameters of the trick
 */
public record TrickMessage(UUID squidId, TrickParameters trickParams) implements CustomPacketPayload {
    public static final Type<TrickMessage> TYPE =
            new Type<>(DataReference.getIdentifier("trick"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, TrickMessage> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, TrickMessage::squidId,
                    TrickParameters.STREAM_CODEC, TrickMessage::trickParams,
                    TrickMessage::new);

    public static void handle(final TrickMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity e = context.player().getVehicle();
            if (e.getUUID().equals(message.squidId())) {
                ((RocketSquidEntity) e).doTrick(message.trickParams());
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
}
