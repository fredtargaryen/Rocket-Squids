package com.fredtargaryen.rocketsquids.network;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.network.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MessageHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DataReference.MODID, "channel"),
            () -> "1.0", //version that will be offered to the server
            (String s) -> s.equals("1.0"), //client accepted versions
            (String s) -> s.equals("1.0"));//server accepted versions

    public static void init() {
        INSTANCE.registerMessage(0, MessageBabyCapData.class, MessageBabyCapData::toBytes, MessageBabyCapData::new, MessageBabyCapData::onMessage);
        INSTANCE.registerMessage(1, MessageAdultCapData.class, MessageAdultCapData::toBytes, MessageAdultCapData::new, MessageAdultCapData::onMessage);
        INSTANCE.registerMessage(2, MessagePlayNoteServer.class, MessagePlayNoteServer::toBytes, MessagePlayNoteServer::new, MessagePlayNoteServer::onMessage);
        INSTANCE.registerMessage(3, MessagePlayNoteClient.class, MessagePlayNoteClient::toBytes, MessagePlayNoteClient::new, MessagePlayNoteClient::onMessage);
        INSTANCE.registerMessage(4, MessageSquidNote.class, MessageSquidNote::toBytes, MessageSquidNote::new, MessageSquidNote::onMessage);
    }
}
