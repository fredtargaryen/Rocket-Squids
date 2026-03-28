// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.ModRocketSquids;
import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MessagePlayNoteServer {
    private byte note;
    private double x;
    private double y;
    private double z;

    @SuppressWarnings("unused")
    public MessagePlayNoteServer() {

    }

    public MessagePlayNoteServer(byte note, double x, double y, double z) {
        this.note = note;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(note > -1 && note < 36) {
                MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
                    assert player != null;
                    return new PacketDistributor.TargetPoint(this.x, this.y, this.z, 64.0, player.level().dimension());
                }), new MessagePlayNoteClient(this.note));
                assert player != null;
                // get the Level, cast to ServerLevel, get all of the entities in the level
                Iterable<Entity> entityIterable = ((ServerLevel) player.level()).getEntities().getAll();
                // for each entity in the level we check if its a rocket squid, then make sure its with in 100 blocks
                for (Entity e : entityIterable) {
                    if (e instanceof RocketSquidEntity) {
                        if (e.position().distanceTo(player.position()) > 100.0D) {
                            e.getCapability(ModRocketSquids.ADULTCAP).ifPresent(cap -> cap.processNote(this.note));
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessagePlayNoteServer(ByteBuf buf) {
        this.note = buf.readByte();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.note);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }
}
