package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderRSFactory implements IRenderFactory<EntityRocketSquid>
{
    private byte growthStage;
    public RenderRSFactory(byte growthStage)
    {
        this.growthStage = growthStage;
    }

    @Override
    public Render<? super EntityRocketSquid> createRenderFor(RenderManager manager)
    {
        if(this.growthStage == 0)
        {
            return new RenderBabyRS(manager, new ModelBabyRocketSquid());
        }
        else if(this.growthStage == 1) {
            return new RenderRS(manager, new ModelRocketSquid());
        }
        else
        {
            return null;
        }
    }
}
