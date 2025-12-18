package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ModelConchArmor extends HumanoidModel<LivingEntity> {

    public ModelPart shape2;
    public ModelPart shape6;
    public ModelPart shape3;
    public ModelPart shape1;

    public ModelConchArmor(float modelSize) {
        super(modelSize);
        this.texWidth = 16;
        this.texHeight = 16;
        this.head = new ModelPart(this, 0, 0);
        this.hat = new ModelPart(this, 32, 0);
        this.shape2 = new ModelPart(this, 0, 0);
        this.shape2.setPos(0.0F, 0.0F, 0.0F);
        this.shape2.addBox(-5.0F, -4.0F, 0.0F, 2, 1, 1, 0.0F);
        this.shape6 = new ModelPart(this, 5, 2);
        this.shape6.setPos(0.0F, 0.0F, 0.0F);
        this.shape6.addBox(-8.0F, -5.0F, 0.0F, 3, 3, 1, 0.0F);
        this.shape3 = new ModelPart(this, 12, 0);
        this.shape3.setPos(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(-9.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F);
        this.shape1 = new ModelPart(this, 6, 6);
        this.shape1.setPos(0.0F, 0.0F, 0.0F);
        this.shape1.addBox(-8.0F, -4.0F, 1.0F, 2, 2, 1, 0.0F);
        this.head.addChild(this.shape2);
        this.head.addChild(this.shape6);
        this.head.addChild(this.shape3);
        this.head.addChild(this.shape1);
    }
}
