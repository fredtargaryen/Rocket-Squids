package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class MessageRSProps implements IMessage, IMessageHandler<MessageRSProps, IMessage>
{
    private int eID;
    private NBTTagCompound props;

    public void fromBytes(ByteBuf buf)
    {
        this.eID = buf.readInt();
        this.props = ByteBufUtils.readTag(buf);
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.eID);
        ByteBufUtils.writeTag(buf, this.props);
    }

    public IMessage onMessage(MessageRSProps message, MessageContext ctx)
    {
        try
        {
            EntityRocketSquid ers = this.getSquid(message.eID);
        }
        catch(NullPointerException n)
        {
        }
        return null;
    }

    public void setProps(int id, NBTTagCompound props)
    {
        this.eID = id;
        this.props = props;
    }

    private EntityRocketSquid getSquid(int id)
    {
        List l = Minecraft.getMinecraft().theWorld.getLoadedEntityList();
        for(int x = 0; x < l.size(); x++)
        {
            Entity e = (Entity)l.get(x);
            if(e.getEntityId() == id)
            {
                return (EntityRocketSquid)e;
            }
        }
        return null;
    }
}
