// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.network;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("removal")
public class MessageHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DataReference.MODID, "channel"),
            () -> "1.0", //version that will be offered to the server
            (String s) -> s.equals("1.0"), //client accepted versions
            (String s) -> s.equals("1.0"));//server accepted versions

    public static void init() {
        INSTANCE.registerMessage(0, MessagePlayNoteServer.class, MessagePlayNoteServer::toBytes, MessagePlayNoteServer::new, MessagePlayNoteServer::onMessage);
        INSTANCE.registerMessage(1, MessagePlayNoteClient.class, MessagePlayNoteClient::toBytes, MessagePlayNoteClient::new, MessagePlayNoteClient::onMessage);
        INSTANCE.registerMessage(2, MessageSquidNote.class, MessageSquidNote::toBytes, MessageSquidNote::new, MessageSquidNote::onMessage);
        INSTANCE.registerMessage(3, MessageSquidFirework.class, MessageSquidFirework::toBytes, MessageSquidFirework::new, MessageSquidFirework::onMessage);
    }
}
