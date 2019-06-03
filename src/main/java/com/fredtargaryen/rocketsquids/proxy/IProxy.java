package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.renderer.entity.model.ModelBiped;

public interface IProxy
{
    void registerRenderers();

    void openConchClient(byte conchStage);

    ModelBiped getConchModel();

    void playNote(byte note);
}
