// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.model.RocketSquidModel;
import com.fredtargaryen.rocketsquids.client.render.state.RocketSquidRenderState;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.fredtargaryen.rocketsquids.client.event.ClientHandler.SQUID_BODY_LAYER;

public class RocketSquidRenderer extends MobRenderer<RocketSquidEntity, RocketSquidRenderState, RocketSquidModel> {
    private static final Identifier normal = DataReference.getIdentifier("textures/entity/rocket_squid.png");
    private static final Identifier blasting = DataReference.getIdentifier("textures/entity/rocket_squid_b.png");
    private final RandomSource random = RandomSource.create();

    public RocketSquidRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context, new RocketSquidModel(context.bakeLayer(SQUID_BODY_LAYER)), 1.0f);
    }

    @Override
    public RocketSquidRenderState createRenderState() {
        return new RocketSquidRenderState();
    }

    @Override
    public void extractRenderState(RocketSquidEntity squid, RocketSquidRenderState state, float partialTick) {
        state.tentacleAngle = squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTick;
        state.xBodyRot = (float) (Mth.lerp(state.partialTick, squid.getPrevRotPitch(), squid.getRotPitch()) * 180 / Math.PI);
        state.yBodyRot = (float) (Mth.lerp(state.partialTick, squid.getPrevRotYaw(), squid.getRotYaw()) * 180 / Math.PI);
        state.saddled = squid.getSaddled();
        state.shaking = squid.getShaking();
        state.blasting = squid.getBlasting();
        state.isInWater = squid.isInWater();
    }

    @Override
    public Vec3 getRenderOffset(RocketSquidRenderState state) {
        Vec3 vec3 = super.getRenderOffset(state);
        if (state.shaking) {
            return vec3.add(this.random.nextGaussian() * 0.02d,
                    this.random.nextGaussian() * 0.02d,
                    this.random.nextGaussian() * 0.02d);
        }
        return vec3;
    }

    @Override
    protected void setupRotations(RocketSquidRenderState state, PoseStack poseStack, float bodyRot, float scale) {
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - state.yBodyRot));
        poseStack.mulPose(Axis.XN.rotationDegrees(state.xBodyRot));
        poseStack.translate(0f, -1.2f, 0f);
    }

    @Override
    public void render(
            RocketSquidRenderState state,
            @NotNull PoseStack matrixStackIn,
            @NotNull MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        if (state.blasting && !state.isInWater) {
            //Choose texture
            TextureAtlasSprite tas = ModelBakery.FIRE_0.sprite();
            //Calculate and set translation-rotation matrix
            matrixStackIn.pushPose();
            VertexConsumer ivb = bufferIn.getBuffer(Sheets.cutoutBlockSheet());
            double yaw_r = state.yBodyRot;
            double pitch_r = state.xBodyRot;
            double adjusted_h_flame_offset = 0.35 * Math.sin(pitch_r);
            matrixStackIn.translate(adjusted_h_flame_offset * Math.sin(yaw_r),
                    0.5 - (0.3 * Math.cos(pitch_r)),
                    -adjusted_h_flame_offset * Math.cos(yaw_r));
            matrixStackIn.mulPose(Axis.of(new Vector3f((float) Math.cos(yaw_r), 0f, (float) Math.sin(yaw_r))).rotation((float) pitch_r));

            //Draw the faces. Advised not to touch any of this; creates a pretty cross of fire which is adjusted to be
            //visible even when squid is blasting directly away from viewer.
            float minu = tas.getU0();
            float minv = tas.getV0();
            float maxu = tas.getU1();
            float maxv = tas.getV1();
            Matrix4f pos = matrixStackIn.last().pose();
            Matrix3f norm = matrixStackIn.last().normal();
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.22f, 0.0f, -0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.16f, -1.5f, -0.28f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.28f, -1.5f, 0.16f, minu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.22f, 0.0f, 0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.22f, 0.0f, 0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.28f, -1.5f, 0.16f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.16f, -1.5f, -0.28f, minu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.22f, 0.0f, -0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.22f, 0.0f, -0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.28f, -1.5f, -0.16f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.16f, -1.5f, 0.28f, minu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.22f, 0.0f, 0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.22f, 0.0f, 0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, 0.16f, -1.5f, 0.28f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.28f, -1.5f, -0.16f, minu, minv, packedLightIn);
            this.doAVertex(ivb, matrixStackIn, pos, norm, -0.22f, 0.0f, -0.22f, minu, maxv, packedLightIn);

            //Clear up
            matrixStackIn.popPose();
        }
        super.render(state, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public @NotNull Identifier getTextureLocation(RocketSquidRenderState state) {
        return state.shaking ? blasting : normal;
    }

    private void doAVertex(VertexConsumer ivb, PoseStack poseStack, Matrix4f pos, Matrix3f norm, float x, float y, float z, float u, float v, int lightLevel) {
        ivb.addVertex(pos, x, y, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightLevel)
                .setNormal(poseStack.last(), 0f, 1f, 0f);
    }
}
