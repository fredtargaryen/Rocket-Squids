package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBabyRocketSquid extends ModelBase
{
    ModelRenderer Head;
    ModelRenderer Tent1;
    ModelRenderer Tent2;
    ModelRenderer Tent3;
    ModelRenderer Tent4;
    ModelRenderer Tent5;
    ModelRenderer Tent6;
    ModelRenderer Tent7;
    ModelRenderer Tent8;

    public ModelBabyRocketSquid()
    {
        textureWidth = 32;
        textureHeight = 32;

        Head = new ModelRenderer(this, 0, 6);
        Head.addBox(-2F, -3F, -2F, 5, 7, 5);
        Head.setRotationPoint(0F, 0F, 0F);
        Head.setTextureSize(32, 32);
        Head.mirror = true;
        setRotation(Head, 0F, 0F, 0F);
        Tent1 = new ModelRenderer(this, 0, 0);
        Tent1.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent1.setRotationPoint(-0.5F, 4F, -0.5F);
        Tent1.setTextureSize(32, 32);
        Tent1.mirror = true;
        setRotation(Tent1, 0F, -2.356194F, 0F);
        Tent2 = new ModelRenderer(this, 0, 0);
        Tent2.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent2.setRotationPoint(0.5F, 4F, -1F);
        Tent2.setTextureSize(32, 32);
        Tent2.mirror = true;
        setRotation(Tent2, 0F, 3.141593F, 0F);
        Tent3 = new ModelRenderer(this, 0, 0);
        Tent3.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent3.setRotationPoint(1.5F, 4F, -0.5F);
        Tent3.setTextureSize(32, 32);
        Tent3.mirror = true;
        setRotation(Tent3, 0F, 2.356194F, 0F);
        Tent4 = new ModelRenderer(this, 0, 0);
        Tent4.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent4.setRotationPoint(2F, 4F, 0.5F);
        Tent4.setTextureSize(32, 32);
        Tent4.mirror = true;
        setRotation(Tent4, 0F, 1.570796F, 0F);
        Tent5 = new ModelRenderer(this, 0, 0);
        Tent5.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent5.setRotationPoint(1.5F, 4F, 1.5F);
        Tent5.setTextureSize(32, 32);
        Tent5.mirror = true;
        setRotation(Tent5, 0F, 0.7853982F, 0F);
        Tent6 = new ModelRenderer(this, 0, 0);
        Tent6.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent6.setRotationPoint(0.5F, 4F, 2F);
        Tent6.setTextureSize(32, 32);
        Tent6.mirror = true;
        setRotation(Tent6, 0F, 0F, 0F);
        Tent7 = new ModelRenderer(this, 0, 0);
        Tent7.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent7.setRotationPoint(-0.5F, 4F, 1.5F);
        Tent7.setTextureSize(32, 32);
        Tent7.mirror = true;
        setRotation(Tent7, 0F, -0.7853982F, 0F);
        Tent8 = new ModelRenderer(this, 0, 0);
        Tent8.addBox(-0.5F, 0F, -0.5F, 1, 5, 1);
        Tent8.setRotationPoint(-1F, 4F, 0.5F);
        Tent8.setTextureSize(32, 32);
        Tent8.mirror = true;
        setRotation(Tent8, 0F, 1.570796F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Head.render(f5);
        Tent1.render(f5);
        Tent2.render(f5);
        Tent3.render(f5);
        Tent4.render(f5);
        Tent5.render(f5);
        Tent6.render(f5);
        Tent7.render(f5);
        Tent8.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
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
