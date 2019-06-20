package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderRSFactory implements IRenderFactory<RocketSquidEntity> {
    @Override
    public EntityRenderer<? super RocketSquidEntity> createRenderFor(EntityRendererManager manager) {
        return new RenderRS(manager, new RocketSquidModel());
    }
}
