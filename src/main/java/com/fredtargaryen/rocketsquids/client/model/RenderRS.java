package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
        //this.addLayer(new LayerFireBlock(this));
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
        if(par1EntitySquid.getBlasting())
        {
            GlStateManager.disableLighting();
            TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
            TextureAtlasSprite tas = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
            GlStateManager.pushMatrix();
            //Larger x goes right; larger y goes ; larger z goes down
            //Need to go right, in and up (-0.43       0.375        0.535)
            GlStateManager.translate(x - 0.36, y + 0.36, z + 0.52);
            GlStateManager.rotate((float)(par1EntitySquid.getRotYaw() * 180 / Math.PI), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)(par1EntitySquid.getRotPitch() * 180 / Math.PI), 1.0F, 0.0F, 0.0F);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float minu = tas.getMinU();
            float minv = tas.getMinV();
            float maxu = tas.getMaxU();
            float maxv = tas.getMaxV();
            //On a squid laying down:
            //Facing top left
            vertexbuffer.pos(-0.18, 0.0, -0.18).tex(maxu, maxv).endVertex();
            vertexbuffer.pos(-0.19, -1.5, -0.19).tex(maxu, minv).endVertex();
            vertexbuffer.pos(0.19, -1.5, 0.19).tex(minu, minv).endVertex();
            vertexbuffer.pos(0.18, 0.0, 0.18).tex(minu, maxv).endVertex();
//            //Facing bottom left
//            vertexbuffer.pos(squidCentreX + 0.18, squidCentreY,         squidCentreZ + 0.18).tex(minu, maxv).endVertex();
//            vertexbuffer.pos(squidCentreX + 0.19, squidCentreY - 1.2,  squidCentreZ + 0.19).tex(minu, minv).endVertex();
//            vertexbuffer.pos(squidCentreX - 0.19, squidCentreY - 1.2,  squidCentreZ - 0.19).tex(maxu, minv).endVertex();
//            vertexbuffer.pos(squidCentreX - 0.18, squidCentreY,         squidCentreZ - 0.18).tex(maxu, maxv).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
        }
        super.doRender(par1EntitySquid, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRocketSquid entity) {
        return entity.getBlasting() || entity.getShaking() ? blasting : normal;
    }
}
