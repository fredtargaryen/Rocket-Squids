package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class LayerFireBlock implements LayerRenderer<EntityRocketSquid>
{
    private static final ResourceLocation fireTexture = new ResourceLocation("textures/blocks/fire_layer_0.png");
    private RenderRS squidRenderer;

    public LayerFireBlock(RenderRS renderer)
    {
        this.squidRenderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityRocketSquid par1EntitySquid, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(par1EntitySquid.getBlasting() && !par1EntitySquid.isInWater())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.36F, 1.2F, 0.36F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.18F, 0.375F, 0.3F);
            int i = par1EntitySquid.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.squidRenderer.bindTexture(fireTexture);
            blockrendererdispatcher.renderBlockBrightness(Blocks.FIRE.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
