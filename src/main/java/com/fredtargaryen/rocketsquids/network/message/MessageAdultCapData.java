package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.capability.adult.IAdultCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageAdultCapData {
    private UUID squidToUpdate;
    private NBTTagCompound capData;

    public MessageAdultCapData() {}

    public MessageAdultCapData(UUID id, IAdultCapability cap) {
        this.squidToUpdate = id;
        this.capData = (NBTTagCompound) RocketSquidsBase.ADULTCAP.writeNBT(cap, null);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            List<Entity> l = Minecraft.getInstance().world.loadedEntityList;
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
        this.capData = new NBTTagCompound();
        this.capData.setDouble("pitch", buf.readDouble());
        this.capData.setDouble("yaw", buf.readDouble());
        this.capData.setDouble("targetPitch", buf.readDouble());
        this.capData.setDouble("targetYaw", buf.readDouble());
        this.capData.setBoolean("shaking", buf.readBoolean());
        this.capData.setBoolean("blasting", buf.readBoolean());
        this.capData.setBoolean("forcedblast", buf.readBoolean());
        this.capData.setByteArray("latestnotes", new byte[] { buf.readByte(), buf.readByte(), buf.readByte() });
        this.capData.setByteArray("targetnotes", new byte[] { buf.readByte(), buf.readByte(), buf.readByte() });
        this.capData.setBoolean("blasttostatue", buf.readBoolean());
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
