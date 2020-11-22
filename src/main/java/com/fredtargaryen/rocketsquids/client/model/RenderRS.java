package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

public class RenderRS extends MobRenderer<RocketSquidEntity, RocketSquidModel> {
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/rs.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID + ":textures/entity/rsb.png");

    public RenderRS(EntityRendererManager rm, RocketSquidModel model)
    {
        super(rm, model, 0.9F);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float handleRotationFloat(RocketSquidEntity squid, float partialTicks) {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void applyRotations(RocketSquidEntity ers, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        float exactPitch = (float) (MathHelper.lerp(partialTicks, ers.getPrevRotPitch(), ers.getRotPitch()) * 180 / Math.PI);
        float exactYaw = (float) (MathHelper.lerp(partialTicks, ers.getPrevRotYaw(), ers.getRotYaw()) * 180 / Math.PI);
        matrixStack.translate(0, 0.5, 0);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180f - exactYaw));
        matrixStack.rotate(Vector3f.XN.rotationDegrees(exactPitch));
        matrixStack.translate(0f, -1.2f, 0f);
    }

    public void render(RocketSquidEntity par1EntitySquid, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (par1EntitySquid.getShaking()) {
            Random r = par1EntitySquid.getRNG();
            matrixStackIn.translate(r.nextGaussian() * 0.02d,r.nextGaussian() * 0.02d,r.nextGaussian() * 0.02d);
        }
        else if(par1EntitySquid.getBlasting() && !par1EntitySquid.isInWater()) {
            //Choose texture
            TextureAtlasSprite tas = ModelBakery.LOCATION_FIRE_0.getSprite();
            //Calculate and set translation-rotation matrix
            matrixStackIn.push();
            IVertexBuilder ivb = bufferIn.getBuffer(Atlases.getCutoutBlockType());
            double yaw_r = par1EntitySquid.getRotYaw();
            double pitch_r = par1EntitySquid.getRotPitch();
            double adjusted_h_flame_offset = 0.35 * Math.sin(pitch_r);
            matrixStackIn.translate(adjusted_h_flame_offset * Math.sin(yaw_r),
                    0.5 - (0.3 * Math.cos(pitch_r)),
                    -adjusted_h_flame_offset * Math.cos(yaw_r));
            matrixStackIn.rotate(new Vector3f((float) Math.cos(yaw_r), 0f, (float) Math.sin(yaw_r)).rotation((float) pitch_r));

            //Prepare to draw
            RenderSystem.disableLighting();
            //GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240f, 240f);
            RenderSystem.color4f(1f, 1f,1f ,1f);
            //this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            //Draw the faces. Advised not to touch any of this; creates a pretty cross of fire which is adjusted to be
            //visible even when squid is blasting directly away from viewer.
            float minu = tas.getMinU();
            float minv = tas.getMinV();
            float maxu = tas.getMaxU();
            float maxv = tas.getMaxV();
            Matrix4f pos = matrixStackIn.getLast().getMatrix();
            Matrix3f norm = matrixStackIn.getLast().getNormal();
            this.doAVertex(ivb, pos, norm,-0.22f, 0.0f, -0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, pos, norm,-0.16f, -1.5f, -0.28f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm,0.28f, -1.5f, 0.16f, minu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm,0.22f, 0.0f, 0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, pos, norm, -0.22f, 0.0f, 0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, pos, norm, -0.28f, -1.5f, 0.16f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, 0.16f, -1.5f, -0.28f, minu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, 0.22f, 0.0f, -0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, pos, norm, 0.22f, 0.0f, -0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, pos, norm, 0.28f, -1.5f, -0.16f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, -0.16f, -1.5f, 0.28f, minu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, -0.22f, 0.0f, 0.22f, minu, maxv, packedLightIn);

            this.doAVertex(ivb, pos, norm, 0.22f, 0.0f, 0.22f, maxu, maxv, packedLightIn);
            this.doAVertex(ivb, pos, norm, 0.16f, -1.5f, 0.28f, maxu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, -0.28f, -1.5f, -0.16f, minu, minv, packedLightIn);
            this.doAVertex(ivb, pos, norm, -0.22f, 0.0f, -0.22f, minu, maxv, packedLightIn);

            //Clear up
            RenderSystem.enableLighting();
            matrixStackIn.pop();
        }
        super.render(par1EntitySquid, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(RocketSquidEntity entity) {
        return entity.getBlasting() || entity.getShaking() ? blasting : normal;
    }

    private void doAVertex(IVertexBuilder ivb, Matrix4f pos, Matrix3f norm, float x, float y, float z, float u, float v, int lightLevel) {
        ivb.pos(pos, x, y, z)
                .color(1f, 1f, 1f, 1f)
                .tex(u, v)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(lightLevel)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
    }
}
