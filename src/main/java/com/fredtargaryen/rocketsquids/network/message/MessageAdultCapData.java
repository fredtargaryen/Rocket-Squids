// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.ModRocketSquids;
import com.fredtargaryen.rocketsquids.content.cap.entity.adult.AdultCap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageAdultCapData {
    private UUID squidToUpdate;
    private CompoundTag capData;

    @SuppressWarnings("unused")
    public MessageAdultCapData() {

    }

    public MessageAdultCapData(UUID id, AdultCap cap) {
        this.squidToUpdate = id;
        this.capData = cap.saveNBT(new CompoundTag());
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            Iterable<Entity> l = Minecraft.getInstance().level.entitiesForRendering();
            Iterator<Entity> squidFinder = l.iterator();
            Entity e;
            while(squidFinder.hasNext()) {
                e = squidFinder.next();
                if(e.getUUID().equals(this.squidToUpdate)) {
                    e.getCapability(ModRocketSquids.ADULTCAP).ifPresent(cap ->
                        // We can assume e is an adult rocket squid
                        cap.loadNBT(this.capData)
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageAdultCapData(ByteBuf buf) {
        this.squidToUpdate = new UUID(buf.readLong(), buf.readLong());
        //Unfortunately have to manually read from the buffer now
        this.capData = new CompoundTag();
        this.capData.putDouble("pitch", buf.readDouble());
        this.capData.putDouble("yaw", buf.readDouble());
        this.capData.putDouble("targetPitch", buf.readDouble());
        this.capData.putDouble("targetYaw", buf.readDouble());
        this.capData.putBoolean("shaking", buf.readBoolean());
        this.capData.putBoolean("blasting", buf.readBoolean());
        this.capData.putBoolean("forcedblast", buf.readBoolean());
        this.capData.putByteArray("latestnotes", new byte[] { buf.readByte(), buf.readByte(), buf.readByte() });
        this.capData.putByteArray("targetnotes", new byte[] { buf.readByte(), buf.readByte(), buf.readByte() });
        this.capData.putBoolean("blasttostatue", buf.readBoolean());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.squidToUpdate.getMostSignificantBits());
        buf.writeLong(this.squidToUpdate.getLeastSignificantBits());
        //Unfortunately have to manually write to the buffer now
        buf.writeDouble(this.capData.getDouble("pitch"));
        buf.writeDouble(this.capData.getDouble("yaw"));
        buf.writeDouble(this.capData.getDouble("targetPitch"));
        buf.writeDouble(this.capData.getDouble("targetYaw"));
        buf.writeBoolean(this.capData.getBoolean("shaking"));
        buf.writeBoolean(this.capData.getBoolean("blasting"));
        buf.writeBoolean(this.capData.getBoolean("forcedblast"));
        buf.writeBytes(this.capData.getByteArray("latestnotes"));
        buf.writeBytes(this.capData.getByteArray("targetnotes"));
        buf.writeBoolean(this.capData.getBoolean("blasttostatue"));
    }
}
