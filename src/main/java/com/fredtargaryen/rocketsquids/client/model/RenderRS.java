package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderRS extends RenderLiving<EntityRocketSquid>
{
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/rs.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID + ":textures/entity/rsb.png");

    public RenderRS(RenderManager rm, ModelRocketSquid model, float shadowSize)
    {
        super(rm, model, shadowSize);
        this.addLayer(new LayerFireBlock(this));
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
    protected void rotateCorpse(EntityRocketSquid ers, float yaw, float pitch, float partialTicks)
    {
        double prp = ers.getPrevRotPitch();
        double rp = ers.getRotPitch();
        //Also convert to degrees.
        float exactPitch = (float) ((prp + (rp - prp) * partialTicks) * 180 / Math.PI);
        double pry = ers.getPrevRotYaw();
        float exactYaw = (float) ((pry + (ers.getRotYaw() - pry) * partialTicks) * 180 / Math.PI);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        GlStateManager.rotate(exactYaw, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(exactPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.2F, 0.0F);
    }

    public void doRender(EntityRocketSquid par1EntitySquid, double x, double y, double z, float par8, float partialTicks)
    {
        if (par1EntitySquid.getShaking())
        {
            Random r = par1EntitySquid.getRNG();
            x += r.nextGaussian() * 0.02D;
            y += r.nextGaussian() * 0.02D;
            z += r.nextGaussian() * 0.02D;
        }
        super.doRender(par1EntitySquid, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRocketSquid entity) {
        return entity.getBlasting() || entity.getShaking() ? blasting : normal;
    }
}
