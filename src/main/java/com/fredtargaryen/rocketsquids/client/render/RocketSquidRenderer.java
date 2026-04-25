// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.model.RocketSquidModel;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.fredtargaryen.rocketsquids.client.event.ClientHandler.SQUID_BODY_LAYER;

@SuppressWarnings("removal")
public class RocketSquidRenderer extends MobRenderer<RocketSquidEntity, RocketSquidModel<RocketSquidEntity>> {
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/rocket_squid.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID + ":textures/entity/rocket_squid_b.png");

    public RocketSquidRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context, new RocketSquidModel<>(context.bakeLayer(SQUID_BODY_LAYER)), 1.0f);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float getBob(
            RocketSquidEntity squid,
            float partialTicks
    ) {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void setupRotations(RocketSquidEntity ers, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        float exactPitch = (float) (Mth.lerp(partialTick, ers.getPrevRotPitch(), ers.getRotPitch()) * 180 / Math.PI);
        float exactYaw = (float) (Mth.lerp(partialTick, ers.getPrevRotYaw(), ers.getRotYaw()) * 180 / Math.PI);

        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - exactYaw));
        poseStack.mulPose(Axis.XN.rotationDegrees(exactPitch));
        poseStack.translate(0f, -1.2f, 0f);
    }

    @Override
    public void render(
            RocketSquidEntity par1EntitySquid,
            float entityYaw,
            float partialTicks,
            @NotNull PoseStack matrixStackIn,
            @NotNull MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        if (par1EntitySquid.getShaking()) {
            RandomSource r = par1EntitySquid.getRandom();
            matrixStackIn.translate(r.nextGaussian() * 0.02d, r.nextGaussian() * 0.02d, r.nextGaussian() * 0.02d);
        } else if (par1EntitySquid.getBlasting() && !par1EntitySquid.isInWater()) {
            //Choose texture
            TextureAtlasSprite tas = ModelBakery.FIRE_0.sprite();
            //Calculate and set translation-rotation matrix
            matrixStackIn.pushPose();
            VertexConsumer ivb = bufferIn.getBuffer(Sheets.cutoutBlockSheet());
            double yaw_r = par1EntitySquid.getRotYaw();
            double pitch_r = par1EntitySquid.getRotPitch();
            double adjusted_h_flame_offset = 0.35 * Math.sin(pitch_r);
            matrixStackIn.translate(adjusted_h_flame_offset * Math.sin(yaw_r),
                    0.5 - (0.3 * Math.cos(pitch_r)),
                    -adjusted_h_flame_offset * Math.cos(yaw_r));
            matrixStackIn.mulPose(Axis.of(new Vector3f((float) Math.cos(yaw_r), 0f, (float) Math.sin(yaw_r))).rotation((float) pitch_r));

            //Prepare to draw
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

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
        super.render(par1EntitySquid, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RocketSquidEntity entity) {
        return entity.getShaking() ? blasting : normal;
    }

    private void doAVertex(VertexConsumer ivb, PoseStack poseStack, Matrix4f pos, Matrix3f norm, float x, float y, float z, float u, float v, int lightLevel) {
        ivb.vertex(pos, x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(lightLevel)
                .normal(poseStack.last(), 0f, 1f, 0f)
                .endVertex();
    }
}
