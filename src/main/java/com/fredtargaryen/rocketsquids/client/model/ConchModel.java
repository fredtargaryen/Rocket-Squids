package com.fredtargaryen.rocketsquids.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ConchModel extends BipedModel<LivingEntity> {

    public ModelRenderer shape2;
    public ModelRenderer shape6;
    public ModelRenderer shape3;
    public ModelRenderer shape1;

    public ConchModel(float modelSize) {
        super(modelSize);
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.shape2 = new ModelRenderer(this, 0, 0);
        this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape2.addBox(-5.0F, -4.0F, 0.0F, 2, 1, 1, 0.0F);
        this.shape6 = new ModelRenderer(this, 5, 2);
        this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape6.addBox(-8.0F, -5.0F, 0.0F, 3, 3, 1, 0.0F);
        this.shape3 = new ModelRenderer(this, 12, 0);
        this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(-9.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F);
        this.shape1 = new ModelRenderer(this, 6, 6);
        this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape1.addBox(-8.0F, -4.0F, 1.0F, 2, 2, 1, 0.0F);
        this.bipedHead.addChild(this.shape2);
        this.bipedHead.addChild(this.shape6);
        this.bipedHead.addChild(this.shape3);
        this.bipedHead.addChild(this.shape1);
    }
}
