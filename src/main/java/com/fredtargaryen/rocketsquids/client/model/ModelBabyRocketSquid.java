package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBabyRocketSquid - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ModelBabyRocketSquid extends ModelBase {
    public ModelRenderer Head;
    public ModelRenderer Tent1;
    public ModelRenderer Tent2;
    public ModelRenderer Tent3;
    public ModelRenderer Tent4;
    public ModelRenderer Tent5;
    public ModelRenderer Tent6;
    public ModelRenderer Tent7;
    public ModelRenderer Tent8;

    public ModelBabyRocketSquid() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.Head = new ModelRenderer(this, 0, 6);
        this.Head.mirror = true;
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-2.0F, -3.0F, -2.0F, 5, 7, 5, 0.0F);

        this.Tent1 = new ModelRenderer(this, 0, 0);
        this.Tent1.setRotationPoint(-0.5F, 4.0F, -0.5F);
        this.Tent1.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent1, 0.0F, -2.356194490192345F, 0.0F);

        this.Tent2 = new ModelRenderer(this, 0, 0);
        this.Tent2.setRotationPoint(0.5F, 4.0F, -1.0F);
        this.Tent2.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent2, 0.0F, 3.141592653589793F, 0.0F);

        this.Tent3 = new ModelRenderer(this, 0, 0);
        this.Tent3.setRotationPoint(1.5F, 4.0F, -0.5F);
        this.Tent3.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent3, 0.0F, 2.356194490192345F, 0.0F);

        this.Tent4 = new ModelRenderer(this, 0, 0);
        this.Tent4.setRotationPoint(2.0F, 4.0F, 0.5F);
        this.Tent4.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent4, 0.0F, 1.5707963267948966F, 0.0F);

        this.Tent5 = new ModelRenderer(this, 0, 0);
        this.Tent5.setRotationPoint(1.5F, 4.0F, 1.5F);
        this.Tent5.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent5, 0.0F, 0.7853981633974483F, 0.0F);

        this.Tent6 = new ModelRenderer(this, 0, 0);
        this.Tent6.setRotationPoint(0.5F, 4.0F, 2.0F);
        this.Tent6.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);

        this.Tent7 = new ModelRenderer(this, 0, 0);
        this.Tent7.setRotationPoint(-0.5F, 4.0F, 1.5F);
        this.Tent7.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent7, 0.0F, -0.7853981633974483F, 0.0F);

        this.Tent8 = new ModelRenderer(this, 0, 0);
        this.Tent8.setRotationPoint(-1.0F, 4.0F, 0.5F);
        this.Tent8.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, 0.0F);
        this.setRotateAngle(Tent8, 0.0F, -1.5707963267948966F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Head.render(f5);
        this.Tent1.render(f5);
        this.Tent2.render(f5);
        this.Tent3.render(f5);
        this.Tent4.render(f5);
        this.Tent5.render(f5);
        this.Tent6.render(f5);
        this.Tent7.render(f5);
        this.Tent8.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setRotationAngles(float time, float maxSpeed, float whatever, float rotationYaw, float rotationPitch, float scale, Entity entity)
    {
        super.setRotationAngles(time, maxSpeed, whatever, rotationYaw, rotationPitch, scale, entity);
        this.Tent1.rotateAngleX = whatever;
        this.Tent2.rotateAngleX = whatever;
        this.Tent3.rotateAngleX = whatever;
        this.Tent4.rotateAngleX = whatever;
        this.Tent5.rotateAngleX = whatever;
        this.Tent6.rotateAngleX = whatever;
        this.Tent7.rotateAngleX = whatever;
        this.Tent8.rotateAngleX = whatever;
    }
}
