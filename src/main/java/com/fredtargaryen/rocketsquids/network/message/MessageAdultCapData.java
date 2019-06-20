package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.capability.adult.IAdultCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageAdultCapData {
    private UUID squidToUpdate;
    private CompoundNBT capData;

    public MessageAdultCapData() {}

    public MessageAdultCapData(UUID id, IAdultCapability cap) {
        this.squidToUpdate = id;
        this.capData = (CompoundNBT) RocketSquidsBase.ADULTCAP.writeNBT(cap, null);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Iterable<Entity> l = Minecraft.getInstance().world.func_217416_b();
            Iterator<Entity> squidFinder = l.iterator();
            Entity e;
            while(squidFinder.hasNext()) {
                e = squidFinder.next();
                if(e.getUniqueID().equals(this.squidToUpdate)) {
                    e.getCapability(RocketSquidsBase.ADULTCAP).ifPresent(cap ->
                        //Can assume e is a rocket squid
                        RocketSquidsBase.ADULTCAP.readNBT(cap, null, this.capData));
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
        this.capData = new CompoundNBT();
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
