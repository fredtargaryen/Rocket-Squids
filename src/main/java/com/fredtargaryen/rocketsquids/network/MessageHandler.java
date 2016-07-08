package com.fredtargaryen.rocketsquids.network;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidCapData;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessageHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DataReference.MODID);

    public static void init()
    {
        INSTANCE.registerMessage(MessageSquidCapData.class, MessageSquidCapData.class, 0, Side.CLIENT);
    }
}
