// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.fredtargaryen.rocketsquids.RSAttachmentTypes.SQUID;

/**
 * Plays a note on the server, for rocket squids to respond to if appropriate.
 * This causes a {@link PlayNoteClientMessage} to be sent to all players around.
 * Direction: client to server
 *
 * @param note The note to play
 * @param x    The x position at which to play the note
 * @param y    The y position at which to play the note
 * @param z    The z position at which to play the note
 */
public record PlayNoteServerMessage(byte note, double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayNoteServerMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("note_server"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PlayNoteServerMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, PlayNoteServerMessage::note,
                    ByteBufCodecs.DOUBLE, PlayNoteServerMessage::x,
                    ByteBufCodecs.DOUBLE, PlayNoteServerMessage::y,
                    ByteBufCodecs.DOUBLE, PlayNoteServerMessage::z,
                    PlayNoteServerMessage::new);

    public static void handle(final PlayNoteServerMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            byte note = message.note();
            if (note > -1 && note < 36) {
                MessageHandler.sendToPlayersNear(
                        (ServerLevel) player.level(),
                        new PlayNoteClientMessage(note),
                        message.x(),
                        message.y(),
                        message.z(),
                        DataReference.PLAYER_HEAR_RANGE);
                Iterable<Entity> entityIterable = ((ServerLevel) player.level()).getEntities().getAll();
                for (Entity e : entityIterable) {
                    if (e instanceof RocketSquidEntity) {
                        if (e.position().distanceTo(player.position()) <= DataReference.SQUID_LISTEN_RANGE) {
                            e.getData(SQUID).processNote(note);
                        }
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
}
