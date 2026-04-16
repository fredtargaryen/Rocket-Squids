// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.model.BabyRocketSquidModel;
import com.fredtargaryen.rocketsquids.level.entity.BabyRocketSquidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.client.event.ClientHandler.BABY_SQUID_BODY_LAYER;

@SuppressWarnings("removal")
public class BabyRocketSquidRenderer extends MobRenderer<BabyRocketSquidEntity, BabyRocketSquidModel<BabyRocketSquidEntity>> {
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/baby_rocket_squid.png");

    public BabyRocketSquidRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context, new BabyRocketSquidModel<>(context.bakeLayer(BABY_SQUID_BODY_LAYER)), 1.0f);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float getBob(
            BabyRocketSquidEntity squid,
            float partialTicks
    ) {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void setupRotations(
            BabyRocketSquidEntity ers,
            PoseStack matrixStack,
            float ageInTicks,
            float rotationYaw,
            float partialTicks
    ) {
        float exactPitch = (float) (Mth.lerp(partialTicks, ers.getPrevRotPitch(), ers.getRotPitch()) * 180 / Math.PI);
        float exactYaw = (float) (Mth.lerp(partialTicks, ers.getPrevRotYaw(), ers.getRotYaw()) * 180 / Math.PI);

        matrixStack.translate(0, 0.15, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180f - exactYaw));
        matrixStack.mulPose(Axis.XN.rotationDegrees(exactPitch));
        matrixStack.translate(0f, -1.3f, 0f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BabyRocketSquidEntity entity) {
        return normal;
    }
}
