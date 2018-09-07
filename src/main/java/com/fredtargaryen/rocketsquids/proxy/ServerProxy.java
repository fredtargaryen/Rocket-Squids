package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.model.ModelBiped;

public class ServerProxy extends CommonProxy
{
    @Override
    public void registerRenderers(){}

    @Override
    public void registerModels(){}

    @Override
    public void openConchClient(byte conchStage){}

    @Override
    public ModelBiped getConchModel() { return null; }

    @Override
    public void playNote(byte note) {}
}
