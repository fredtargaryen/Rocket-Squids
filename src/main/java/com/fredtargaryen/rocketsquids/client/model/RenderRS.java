package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderRS extends RenderLiving<EntityRocketSquid>
{
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/rs.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID + ":textures/entity/rsb.png");
    private static final ResourceLocation fireTexture = new ResourceLocation("textures/blocks/fire_layer_0.png");

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
    protected void rotateCorpse(EntityRocketSquid ers, float yaw, float pitch, float partialTicks)
    {
        double prp = ers.getPrevRotPitch();
        double rp = ers.getRotPitch();
        //Also convert to degrees.
        float exactPitch = (float) ((prp + (rp - prp) * partialTicks) * 180 / Math.PI);
        double pry = ers.getPrevRotYaw();
        float exactYaw = (float) ((pry + (ers.getRotYaw() - pry) * partialTicks) * 180 / Math.PI);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        //GlStateManager.rotate(180.0F - exactPitch, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(exactYaw, 0.0F, 1.0F, 0.0F);
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
        else if(par1EntitySquid.getBlasting() && !par1EntitySquid.isInWater())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 10.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(0.9F, 1.2F, 0.9F);
            int i = par1EntitySquid.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(fireTexture);
            blockrendererdispatcher.renderBlockBrightness(Blocks.FIRE.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
        super.doRender(par1EntitySquid, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRocketSquid entity) {
        return entity.getBlasting() || entity.getShaking() ? blasting : normal;
    }
}
