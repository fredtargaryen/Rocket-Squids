package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.PrimalSquidAEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderPrimalSquidAFactory implements IRenderFactory<PrimalSquidAEntity> {
    @Override
    public EntityRenderer<? super PrimalSquidAEntity> createRenderFor(EntityRendererManager manager) {
        return new RenderPrimalSquidA(manager, new PrimalSquidAModel());
    }
}
