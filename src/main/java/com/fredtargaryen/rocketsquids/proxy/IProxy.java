package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.renderer.entity.model.BipedModel;

public interface IProxy
{
    void registerRenderers();

    void openConchClient(byte conchStage);

    BipedModel getConchModel();

    void playNote(byte note);
}
