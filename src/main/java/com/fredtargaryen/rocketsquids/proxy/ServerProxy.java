package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.renderer.entity.model.BipedModel;

public class ServerProxy implements IProxy {
    @Override
    public void registerRenderers(){}

    @Override
    public void registerRenderTypes(){}

    @Override
    public void openConchClient(byte conchStage){}

    @Override
    public BipedModel getConchModel() { return null; }

    @Override
    public void playNoteFromMessage(byte note) {}

    @Override
    public void playNoteFromMessageConchNeeded(byte note) {}
}
