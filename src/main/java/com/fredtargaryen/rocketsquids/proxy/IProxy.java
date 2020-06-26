package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.renderer.entity.model.BipedModel;

public interface IProxy
{
    void registerRenderers();

    void registerRenderTypes();

    void openConchClient(byte conchStage);

    BipedModel getConchModel();

    void playNoteFromMessage(byte note);

    void playNoteFromMessageConchNeeded(byte note);
}
