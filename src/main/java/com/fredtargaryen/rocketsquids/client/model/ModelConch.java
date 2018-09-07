package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ModelConch extends ModelBiped {
    public static final ResourceLocation CONCH_TEX = new ResourceLocation(DataReference.MODID + ":textures/armour/conch.png");

    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape6;

    public ModelConch() {
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.shape1 = new ModelRenderer(this, 6, 6);
        this.shape1.setRotationPoint(-7.0F, -4.0F, -2.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);

        this.shape2 = new ModelRenderer(this, 0, 0);
        this.shape2.setRotationPoint(-4.0F, -4.0F, -3.0F);
        this.shape2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);

        this.shape6 = new ModelRenderer(this, 5, 2);
        this.shape6.setRotationPoint(-7.0F, -5.0F, -3.0F);
        this.shape6.addBox(0.0F, 0.0F, 0.0F, 3, 3, 1, 0.0F);

        this.shape3 = new ModelRenderer(this, 12, 0);
        this.shape3.setRotationPoint(-8.0F, -4.0F, -3.0F);
        this.shape3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(CONCH_TEX);
        this.shape2.rotateAngleY += Math.toRadians(entity.getRotationYawHead());
        this.shape2.rotateAngleX += Math.toRadians(entity.rotationPitch);
        this.shape2.render(f5);
        this.shape6.rotateAngleY += Math.toRadians(entity.getRotationYawHead());
        this.shape6.rotateAngleX += Math.toRadians(entity.rotationPitch);
        this.shape6.render(f5);
        this.shape3.rotateAngleY += Math.toRadians(entity.getRotationYawHead());
        this.shape3.rotateAngleX += Math.toRadians(entity.rotationPitch);
        this.shape3.render(f5);
        this.shape1.rotateAngleY += Math.toRadians(entity.getRotationYawHead());
        this.shape1.rotateAngleX += Math.toRadians(entity.rotationPitch);
        this.shape1.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
