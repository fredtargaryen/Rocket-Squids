package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Iterator;
import java.util.function.Supplier;

public class MessagePlayNoteServer {
    private byte note;
    private double x;
    private double y;
    private double z;

    public MessagePlayNoteServer() {}

    public MessagePlayNoteServer(byte note, double x, double y, double z) {
        this.note = note;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if(note > -1 && note < 36) {
                MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(this.x, this.y, this.z, 64.0, player.world.getDimensionKey())), new MessagePlayNoteClient(this.note));
                ((ServerWorld) player.world).getEntities().forEach(e ->
                        e.getCapability(RocketSquidsBase.ADULTCAP).ifPresent(cap -> cap.processNote(this.note)));
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
