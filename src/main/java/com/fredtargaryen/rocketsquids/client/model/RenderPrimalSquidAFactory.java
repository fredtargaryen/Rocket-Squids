package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityPrimalSquidA;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderPrimalSquidAFactory implements IRenderFactory<EntityPrimalSquidA>
{
    @Override
    public Render<? super EntityPrimalSquidA> createRenderFor(RenderManager manager)
    {
        return new RenderPrimalSquidA(manager, new ModelPrimalSquidA());
    }
}
