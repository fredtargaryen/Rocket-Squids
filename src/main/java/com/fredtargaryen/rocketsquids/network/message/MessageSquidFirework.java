package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageSquidFirework {
    private UUID uuid;

    @SuppressWarnings("unused")
    public MessageSquidFirework() {

    }

    public MessageSquidFirework(UUID uuid) {
        this.uuid = uuid;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            Iterable<Entity> l = Minecraft.getInstance().level.entitiesForRendering();
            Iterator<Entity> squidFinder = l.iterator();
            Entity entity;
            while(squidFinder.hasNext()) {
                entity = squidFinder.next();
                if (entity.getUUID().equals(this.uuid)) {
                    RocketSquidEntity rocketSquidEntity = (RocketSquidEntity) entity;
                    rocketSquidEntity.doFireworkParticles();
                    break;
                }
            }
        ctx.get().setPacketHandled(true);
        });
    }

    public MessageSquidFirework(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.uuid.getMostSignificantBits());
        buf.writeLong(this.uuid.getLeastSignificantBits());
    }
}
