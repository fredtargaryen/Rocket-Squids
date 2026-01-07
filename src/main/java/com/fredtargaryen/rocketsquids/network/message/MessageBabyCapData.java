package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.cap.entity.baby.BabyCap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageBabyCapData {
    private UUID squidToUpdate;
    private CompoundTag capData;

    @SuppressWarnings("unused")
    public MessageBabyCapData() {

    }

    public MessageBabyCapData(UUID id, BabyCap cap) {
        this.squidToUpdate = id;
        this.capData = cap.loadNBT(new CompoundTag());
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
                    e.getCapability(RocketSquidsBase.BABYCAP).ifPresent(cap ->
                            // We can assume e is a baby rocket squid
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
    public MessageBabyCapData(ByteBuf buf) {
        this.squidToUpdate = new UUID(buf.readLong(), buf.readLong());
        //Unfortunately have to manually read from the buffer now
        this.capData = new CompoundTag();
        this.capData.putDouble("pitch", buf.readDouble());
        this.capData.putDouble("yaw", buf.readDouble());
        this.capData.putDouble("targetPitch", buf.readDouble());
        this.capData.putDouble("targetYaw", buf.readDouble());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.squidToUpdate.getMostSignificantBits());
        buf.writeLong(this.squidToUpdate.getLeastSignificantBits());
        //Unfortunately have to manually write to the buffer now
        buf.writeDouble(this.capData.getDouble("pitch"));
        buf.writeDouble(this.capData.getDouble("yaw"));
        buf.writeDouble(this.capData.getDouble("targetPitch"));
        buf.writeDouble(this.capData.getDouble("targetYaw"));
    }
}
