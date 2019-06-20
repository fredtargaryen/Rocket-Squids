package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBabyRSFactory implements IRenderFactory<BabyRocketSquidEntity> {
    @Override
    public EntityRenderer<? super BabyRocketSquidEntity> createRenderFor(EntityRendererManager manager) {
        return new RenderBabyRS(manager, new BabyRocketSquidModel());
    }
}
