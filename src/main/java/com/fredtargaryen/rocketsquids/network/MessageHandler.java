// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.network.message.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DataReference.MODID)
public class MessageHandler {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);

        registrar.playToClient(
                AdultCapDataMessage.TYPE,
                AdultCapDataMessage.STREAM_CODEC,
                AdultCapDataMessage::handle
        );

        registrar.playToClient(
                BabyCapDataMessage.TYPE,
                BabyCapDataMessage.STREAM_CODEC,
                BabyCapDataMessage::handle
        );

        registrar.playToClient(
                PlayNoteClientMessage.TYPE,
                PlayNoteClientMessage.STREAM_CODEC,
                PlayNoteClientMessage::handle
        );

        registrar.playToServer(
                PlayNoteServerMessage.TYPE,
                PlayNoteServerMessage.STREAM_CODEC,
                PlayNoteServerMessage::handle
        );

        registrar.playToClient(
                SquidFireworkMessage.TYPE,
                SquidFireworkMessage.STREAM_CODEC,
                SquidFireworkMessage::handle
        );

        registrar.playToClient(
                SquidNoteMessage.TYPE,
                SquidNoteMessage.STREAM_CODEC,
                SquidNoteMessage::handle
        );
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static void sendToPlayersNear(ServerLevel level, CustomPacketPayload message, double x, double y, double z, double radius) {
        PacketDistributor.sendToPlayersNear(level, null, x, y, z, radius, message);
    }
}
