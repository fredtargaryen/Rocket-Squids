package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import com.fredtargaryen.rocketsquids.entity.capability.SquidCapStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MessageSquidCapData implements IMessage, IMessageHandler<MessageSquidCapData, IMessage>
{
    private UUID squidToUpdate;
    private NBTTagCompound capData;

    public MessageSquidCapData() {}

    public MessageSquidCapData(UUID id, ISquidCapability cap)
    {
        this.squidToUpdate = id;
        this.capData = (NBTTagCompound) RocketSquidsBase.SQUIDCAP.writeNBT(cap, null);
    }

    public IMessage onMessage(final MessageSquidCapData message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run()
            {
                List<Entity> l = Minecraft.getMinecraft().theWorld.getLoadedEntityList();
                Iterator<Entity> squidFinder = l.iterator();
                Entity e;
                while(squidFinder.hasNext())
                {
                    e = squidFinder.next();
                    if(e.getPersistentID().equals(MessageSquidCapData.this.squidToUpdate))
                    {
                        if(e.hasCapability(RocketSquidsBase.SQUIDCAP, null)) {
                            //Can assume e is a rocket squid
                            RocketSquidsBase.SQUIDCAP.readNBT(e.getCapability(RocketSquidsBase.SQUIDCAP, null), null, message.capData);
                        }
                    }
                }
            }
        });
        return null;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.squidToUpdate = new UUID(buf.readLong(), buf.readLong());
        this.capData = ByteBufUtils.readTag(buf);
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.squidToUpdate.getMostSignificantBits());
        buf.writeLong(this.squidToUpdate.getLeastSignificantBits());
        ByteBufUtils.writeTag(buf, this.capData);
    }
}
