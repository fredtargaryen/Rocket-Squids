package com.fredtargaryen.rocketsquids.proxy;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ServerProxy implements IProxy {
    @Override
    public void clientSetup(FMLClientSetupEvent event) {

    }

    @Override
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    @Override
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    @Override
    public void registerRenderTypes() {

    }

    @Override
    public void openConchClient(byte conchStage) {

    }

    @Override
    public void playNoteFromMessage(byte note) {

    }

    @Override
    public void playNoteFromMessageConchNeeded(byte note) {

    }
}
