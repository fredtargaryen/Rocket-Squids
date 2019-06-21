package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ConchModel extends BipedModel<LivingEntity> {

    public RendererModel shape2;
    public RendererModel shape6;
    public RendererModel shape3;
    public RendererModel shape1;

    public ConchModel() {
        super();
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.field_78116_c = new RendererModel(this, 0, 0);
        this.field_178720_f = new RendererModel(this, 32, 0);
        this.shape2 = new RendererModel(this, 0, 0);
        this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape2.addBox(-5.0F, -4.0F, 0.0F, 2, 1, 1, 0.0F);
        this.shape6 = new RendererModel(this, 5, 2);
        this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape6.addBox(-8.0F, -5.0F, 0.0F, 3, 3, 1, 0.0F);
        this.shape3 = new RendererModel(this, 12, 0);
        this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(-9.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F);
        this.shape1 = new RendererModel(this, 6, 6);
        this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape1.addBox(-8.0F, -4.0F, 1.0F, 2, 2, 1, 0.0F);
        this.field_78116_c.addChild(this.shape2);
        this.field_78116_c.addChild(this.shape6);
        this.field_78116_c.addChild(this.shape3);
        this.field_78116_c.addChild(this.shape1);
    }

    @Override
    public void render(LivingEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        func_212844_a_(entity, f, f1, f2, f3, f4, f5);
    }
}
