package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBabyRS extends RenderLiving<EntityBabyRocketSquid>
{
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/brs.png");
    public RenderBabyRS(RenderManager rm, ModelBabyRocketSquid model)
    {
        super(rm, model, 0.4F);
    }
    public static float BEFORE_ROT_OFFSET = 0.0F;
    public static float AFTER_ROT_OFFSET = 0.0F;

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float handleRotationFloat(EntityBabyRocketSquid squid, float partialTicks)
    {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void applyRotations(EntityBabyRocketSquid ers, float yaw, float pitch, float partialTicks)
    {
        double prp = ers.getPrevRotPitch();
        double rp = ers.getRotPitch();
        //Also convert to degrees.
        float exactPitch = (float) ((prp + (rp - prp) * partialTicks) * 180 / Math.PI);
        double pry = ers.getPrevRotYaw();
        float exactYaw = (float) ((pry + (ers.getRotYaw() - pry) * partialTicks) * 180 / Math.PI);
        //Previously 0.5F
        GlStateManager.translate(0.0F, 0.15F + BEFORE_ROT_OFFSET, 0.0F);
        GlStateManager.rotate(180.0F - exactYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(exactPitch, -1.0F, 0.0F, 0.0F);
        //Previously 1.2F
        GlStateManager.translate(0.0F, -1.3F + AFTER_ROT_OFFSET, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBabyRocketSquid entity) {
        return normal;
    }
}
