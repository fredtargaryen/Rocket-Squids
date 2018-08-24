package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.model.ModelBiped;

public interface IProxy
{
    void registerRenderers();

    void registerModels();

    void openConchClient(byte conchStage);

    ModelBiped getConchModel();
}
