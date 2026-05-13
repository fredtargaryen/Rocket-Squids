// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.model.BabyRocketSquidModel;
import com.fredtargaryen.rocketsquids.client.render.state.BabyRocketSquidRenderState;
import com.fredtargaryen.rocketsquids.level.entity.BabyRocketSquidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.client.event.ClientHandler.BABY_SQUID_BODY_LAYER;

public class BabyRocketSquidRenderer extends MobRenderer<BabyRocketSquidEntity, BabyRocketSquidRenderState, BabyRocketSquidModel> {
    private static final Identifier normal = DataReference.getIdentifier("textures/entity/baby_rocket_squid.png");

    public BabyRocketSquidRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context, new BabyRocketSquidModel(context.bakeLayer(BABY_SQUID_BODY_LAYER)), 1.0f);
    }

    @Override
    public BabyRocketSquidRenderState createRenderState() {
        return new BabyRocketSquidRenderState();
    }

    @Override
    public void extractRenderState(BabyRocketSquidEntity squid, BabyRocketSquidRenderState state, float partialTick) {
        state.tentacleAngle = squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTick;
        state.xBodyRot = (float) (Mth.lerp(state.partialTick, squid.getPrevRotPitch(), squid.getRotPitch()) * 180 / Math.PI);
        state.yBodyRot = (float) (Mth.lerp(state.partialTick, squid.getPrevRotYaw(), squid.getRotYaw()) * 180 / Math.PI);
    }

    @Override
    protected void setupRotations(BabyRocketSquidRenderState state, PoseStack poseStack, float bodyRot, float scale) {
        poseStack.translate(0, 0.15, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - state.yBodyRot));
        poseStack.mulPose(Axis.XN.rotationDegrees(state.xBodyRot));
        poseStack.translate(0f, -1.3f, 0f);
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull BabyRocketSquidRenderState state) {
        return normal;
    }
}
