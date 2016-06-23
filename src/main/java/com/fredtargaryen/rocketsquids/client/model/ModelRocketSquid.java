package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRocketSquid extends ModelBase
{
    //fields
    ModelRenderer Head;
    ModelRenderer Tent0;
    ModelRenderer Tent1;
    ModelRenderer Tent2;
    ModelRenderer Tent3;
    ModelRenderer Tent4;
    ModelRenderer Tent7;
    ModelRenderer Tent6;
    ModelRenderer Tent5;
  
    public ModelRocketSquid()
    {
        textureWidth = 128;
        textureHeight = 64;
    
        Head = new ModelRenderer(this, 0, 30);
        Head.addBox(-7F, -10F, -7F, 14, 20, 14);
        Head.setRotationPoint(0F, 0F, 0F);
        Head.setTextureSize(128, 64);
        Head.mirror = true;
        setRotation(Head, 0F, 0F, 0F);
        Tent0 = new ModelRenderer(this, 0, 0);
        Tent0.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent0.setRotationPoint(0F, 9F, -5F);
        Tent0.setTextureSize(128, 64);
        Tent0.mirror = true;
        setRotation(Tent0, 0F, 3.141593F, 0F);
        Tent1 = new ModelRenderer(this, 0, 0);
        Tent1.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent1.setRotationPoint(5F, 9F, -5F);
        Tent1.setTextureSize(128, 64);
        Tent1.mirror = true;
        setRotation(Tent1, 0F, 2.356194F, 0F);
        Tent2 = new ModelRenderer(this, 0, 0);
        Tent2.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent2.setRotationPoint(5F, 9F, 0F);
        Tent2.setTextureSize(128, 64);
        Tent2.mirror = true;
        setRotation(Tent2, 0F, 1.570796F, 0F);
        Tent3 = new ModelRenderer(this, 0, 0);
        Tent3.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent3.setRotationPoint(5F, 9F, 5F);
        Tent3.setTextureSize(128, 64);
        Tent3.mirror = true;
        setRotation(Tent3, 0F, 0.7853982F, 0F);
        Tent4 = new ModelRenderer(this, 0, 0);
        Tent4.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent4.setRotationPoint(0F, 9F, 5F);
        Tent4.setTextureSize(128, 64);
        Tent4.mirror = true;
        setRotation(Tent4, 0F, 0F, 0F);
        Tent7 = new ModelRenderer(this, 0, 0);
        Tent7.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent7.setRotationPoint(-5F, 9F, -5F);
        Tent7.setTextureSize(128, 64);
        Tent7.mirror = true;
        setRotation(Tent7, 0F, -2.356194F, 0F);
        Tent6 = new ModelRenderer(this, 0, 0);
        Tent6.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent6.setRotationPoint(-5F, 9F, 0F);
        Tent6.setTextureSize(128, 64);
        Tent6.mirror = true;
        setRotation(Tent6, 0F, -1.570796F, 0F);
        Tent5 = new ModelRenderer(this, 0, 0);
        Tent5.addBox(-1F, -1F, -1F, 2, 20, 2);
        Tent5.setRotationPoint(-5F, 9F, 5F);
        Tent5.setTextureSize(128, 64);
        Tent5.mirror = true;
        setRotation(Tent5, 0F, -0.7853982F, 0F);
    }
  
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Head.render(f5);
        Tent0.render(f5);
        Tent1.render(f5);
        Tent2.render(f5);
        Tent3.render(f5);
        Tent4.render(f5);
        Tent7.render(f5);
        Tent6.render(f5);
        Tent5.render(f5);
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
        this.Tent0.rotateAngleX = whatever;
        this.Tent1.rotateAngleX = whatever;
        this.Tent2.rotateAngleX = whatever;
        this.Tent3.rotateAngleX = whatever;
        this.Tent4.rotateAngleX = whatever;
        this.Tent5.rotateAngleX = whatever;
        this.Tent6.rotateAngleX = whatever;
        this.Tent7.rotateAngleX = whatever;
        EntityRocketSquid ers = (EntityRocketSquid) entity;
        this.Head.rotateAngleY = ers.currentSpin;
        this.Tent0.rotateAngleY = ers.currentSpin;
        this.Tent1.rotateAngleY = ers.currentSpin;
        this.Tent2.rotateAngleY = ers.currentSpin;
        this.Tent3.rotateAngleY = ers.currentSpin;
        this.Tent4.rotateAngleY = ers.currentSpin;
        this.Tent5.rotateAngleY = ers.currentSpin;
        this.Tent6.rotateAngleY = ers.currentSpin;
        this.Tent7.rotateAngleY = ers.currentSpin;
    }
}
