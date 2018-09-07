package com.fredtargaryen.rocketsquids.network;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteClient;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidCapData;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidNote;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessageHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DataReference.MODID);

    public static void init()
    {
        INSTANCE.registerMessage(MessageSquidCapData.class, MessageSquidCapData.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessagePlayNoteServer.class, MessagePlayNoteServer.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessagePlayNoteClient.class, MessagePlayNoteClient.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MessageSquidNote.class, MessageSquidNote.class, 3, Side.CLIENT);
    }
}
