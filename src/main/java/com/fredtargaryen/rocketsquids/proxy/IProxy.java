package com.fredtargaryen.rocketsquids.proxy;

public interface IProxy
{
    void registerRenderers();

    void registerModels();

    void openConchClient(byte conchStage);
}
