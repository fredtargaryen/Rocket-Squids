package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBabyRSFactory implements IRenderFactory<EntityBabyRocketSquid>
{
    @Override
    public Render<? super EntityBabyRocketSquid> createRenderFor(RenderManager manager)
    {
        return new RenderBabyRS(manager, new ModelBabyRocketSquid());
    }
}
