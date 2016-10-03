package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderRSFactory implements IRenderFactory<EntityRocketSquid>
{
    @Override
    public Render<? super EntityRocketSquid> createRenderFor(RenderManager manager)
    {
        return new RenderRS(manager, new ModelRocketSquid());
    }
}
