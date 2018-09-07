package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MessagePlayNoteServer implements IMessage, IMessageHandler<MessagePlayNoteServer, IMessage> {
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

    public IMessage onMessage(final MessagePlayNoteServer message, MessageContext ctx) {
        final EntityPlayerMP epmp = ctx.getServerHandler().player;
        final IThreadListener serverListener = epmp.getServerWorld();
        serverListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                MessageHandler.INSTANCE.sendToAllAround(new MessagePlayNoteClient(message.note), new NetworkRegistry.TargetPoint(epmp.dimension, message.x, message.y, message.z, 64.0));
                List<Entity> l = Minecraft.getMinecraft().world.getLoadedEntityList();
                Iterator<Entity> squidFinder = l.iterator();
                Entity e;
                while(squidFinder.hasNext())
                {
                    e = squidFinder.next();
                    if(e.hasCapability(RocketSquidsBase.SQUIDCAP, null)) {
                        //Can assume e is a rocket squid
                        e.getCapability(RocketSquidsBase.SQUIDCAP, null).processNote(message.note);
                    }
                }
            }
        });
        return null;
    }

    public void fromBytes(ByteBuf buf) {
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
