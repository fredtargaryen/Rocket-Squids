package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import java.util.Random;

public class RenderRS extends RenderLiving<EntityRocketSquid>
{
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/rs.png");
    private static final ResourceLocation blasting = new ResourceLocation(DataReference.MODID + ":textures/entity/rsb.png");

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
        GlStateManager.rotate(180.0F - exactYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(exactPitch, -1.0F, 0.0F, 0.0F);
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
            //Choose texture
            TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
            TextureAtlasSprite tas = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");

            //Calculate and set translation-rotation matrix
            GlStateManager.pushMatrix();
            double yaw_r = par1EntitySquid.getRotYaw();
            double pitch_r = par1EntitySquid.getRotPitch();
            double adjusted_h_flame_offset = 0.35 * Math.sin(pitch_r);
            GlStateManager.translate(
                    x + (adjusted_h_flame_offset * Math.sin(yaw_r)),
                    y + 0.5 - (0.3 * Math.cos(pitch_r)),
                    z - (adjusted_h_flame_offset * Math.cos(yaw_r)));
            GlStateManager.rotate((float)(pitch_r * 180 / Math.PI), (float) Math.cos(yaw_r), 0.0F, (float) Math.sin(yaw_r));

            //Prepare to draw
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            //Draw the faces. Advised not to touch any of this; creates a pretty cross of fire which is adjusted to be
            //visible even when squid is blasting directly away from viewer.
            float minu = tas.getMinU();
            float minv = tas.getMinV();
            float maxu = tas.getMaxU();
            float maxv = tas.getMaxV();

            vertexbuffer.pos(-0.22, 0.0, -0.22).tex(maxu, maxv).endVertex();
            vertexbuffer.pos(-0.16, -1.5, -0.28).tex(maxu, minv).endVertex();
            vertexbuffer.pos(0.28, -1.5, 0.16).tex(minu, minv).endVertex();
            vertexbuffer.pos(0.22, 0.0, 0.22).tex(minu, maxv).endVertex();

            vertexbuffer.pos(-0.22, 0.0, 0.22).tex(maxu, maxv).endVertex();
            vertexbuffer.pos(-0.28, -1.5, 0.16).tex(maxu, minv).endVertex();
            vertexbuffer.pos(0.16, -1.5, -0.28).tex(minu, minv).endVertex();
            vertexbuffer.pos(0.22, 0.0, -0.22).tex(minu, maxv).endVertex();

            vertexbuffer.pos(0.22, 0.0, -0.22).tex(maxu, maxv).endVertex();
            vertexbuffer.pos(0.28, -1.5, -0.16).tex(maxu, minv).endVertex();
            vertexbuffer.pos(-0.16, -1.5, 0.28).tex(minu, minv).endVertex();
            vertexbuffer.pos(-0.22, 0.0, 0.22).tex(minu, maxv).endVertex();

            vertexbuffer.pos(0.22, 0.0, 0.22).tex(maxu, maxv).endVertex();
            vertexbuffer.pos(0.16, -1.5, 0.28).tex(maxu, minv).endVertex();
            vertexbuffer.pos(-0.28, -1.5, -0.16).tex(minu, minv).endVertex();
            vertexbuffer.pos(-0.22, 0.0, -0.22).tex(minu, maxv).endVertex();

            tessellator.draw();

            //Clear up
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
        super.doRender(par1EntitySquid, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRocketSquid entity) {
        return entity.getBlasting() || entity.getShaking() ? blasting : normal;
    }
}
