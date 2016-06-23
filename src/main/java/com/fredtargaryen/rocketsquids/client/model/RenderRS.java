package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderRS extends RenderLiving<EntityRocketSquid>
{
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID, "textures/models/rs.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID, "textures/models/rsb.png");

    public RenderRS(RenderManager rm, ModelRocketSquid model, float shadowSize)
    {
        super(rm, model, shadowSize);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float handleRotationFloat(EntityRocketSquid squid, float partialTicks)
    {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void rotateCorpse(EntityRocketSquid ers, float par2, float par3, float par4)
    {
        float f3 = ers.prevRotationPitch + (ers.rotationPitch - ers.prevRotationPitch) * par4;
        float f4 = ers.prevRotationYaw + (ers.rotationYaw - ers.prevRotationYaw) * par4;
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        GlStateManager.rotate(180.0F - par3, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f4, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.2F, 0.0F);
    }

    public void doRender(EntityRocketSquid par1EntitySquid, double x, double y, double z, float par8, float partialTicks)
    {
        //How to get f to
        float f = par1EntitySquid.prevSpin + (par1EntitySquid.currentSpin - par1EntitySquid.prevSpin) * partialTicks;
        if (par1EntitySquid.getPhase() == EntityRocketSquid.Phase.SHAKE)
        {
            Random r = par1EntitySquid.getRNG();
            x += r.nextGaussian() * 0.02D;
            y += r.nextGaussian() * 0.02D;
            z += r.nextGaussian() * 0.02D;
        }
        else if(par1EntitySquid.getPhase() == EntityRocketSquid.Phase.BLAST)
        {
            //Put the fire here
        }
        super.doRender(par1EntitySquid, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRocketSquid entity) {
        return entity.getBlasting() ? blasting : normal;
    }
}
