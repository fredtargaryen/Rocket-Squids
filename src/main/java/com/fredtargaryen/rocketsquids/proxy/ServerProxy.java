package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.model.HumanoidModel;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ServerProxy implements IProxy {
    @Override
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {}

    @Override
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {}

    @Override
    public void registerRenderTypes() {}

    @Override
    public void openConchClient(byte conchStage) {}

    @Override
    public HumanoidModel<?> getConchModel() { return null; }

    @Override
    public void playNoteFromMessage(byte note) {}

    @Override
    public void playNoteFromMessageConchNeeded(byte note) {}
}
