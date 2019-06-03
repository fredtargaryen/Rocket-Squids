package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.renderer.entity.model.ModelBiped;

public class ServerProxy implements IProxy {
    @Override
    public void registerRenderers(){}

    @Override
    public void openConchClient(byte conchStage){}

    @Override
    public ModelBiped getConchModel() { return null; }

    @Override
    public void playNote(byte note) {}
}
