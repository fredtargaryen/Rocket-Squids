package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderBabyRS extends MobRenderer<BabyRocketSquidEntity, BabyRocketSquidModel> {
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/brs.png");
    public RenderBabyRS(EntityRendererManager rm, BabyRocketSquidModel model)
    {
        super(rm, model, 0.4F);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float handleRotationFloat(BabyRocketSquidEntity squid, float partialTicks) {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void applyRotations(BabyRocketSquidEntity ers, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        float exactPitch = (float) (MathHelper.lerp(partialTicks, ers.getPrevRotPitch(), ers.getRotPitch()) * 180 / Math.PI);
        float exactYaw = (float) (MathHelper.lerp(partialTicks, ers.getPrevRotYaw(), ers.getRotYaw()) * 180 / Math.PI);
        //0.5F for adults
        matrixStack.translate(0, 0.15, 0);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180f - exactYaw));
        matrixStack.rotate(Vector3f.XN.rotationDegrees(exactPitch));
        //1.2F for adults
        matrixStack.translate(0f, -1.3f, 0f);
    }

    @Override
    public ResourceLocation getEntityTexture(BabyRocketSquidEntity entity) {
        return normal;
    }
}
