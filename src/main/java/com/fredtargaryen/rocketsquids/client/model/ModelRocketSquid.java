package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRocketSquid extends ModelBase
{
    private final ModelRenderer Head;
    private final ModelRenderer Tent0;
    private final ModelRenderer Tent1;
    private final ModelRenderer Tent2;
    private final ModelRenderer Tent3;
    private final ModelRenderer Tent4;
    private final ModelRenderer Tent7;
    private final ModelRenderer Tent6;
    private final ModelRenderer Tent5;
    private final ModelRenderer Saddle;
    private final ModelRenderer Straps;
  
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

        Saddle = new ModelRenderer(this, 106, 53);
        Saddle.addBox(-5F, 0F, 7F, 10, 10, 1);
        Saddle.setRotationPoint(0F, 0F, 0F);
        Saddle.setTextureSize(128, 64);
        Saddle.mirror = true;
        setRotation(Saddle, 0F, 0F, 0F);

        Straps = new ModelRenderer(this, 68, 0);
        Straps.addBox(-7.5F, 1F, -7.5F, 15, 8, 15);
        Straps.setRotationPoint(0F, 0F, 0F);
        Straps.setTextureSize(128, 64);
        Straps.mirror = true;
        setRotation(Straps, 0F, 0F, 0F);
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
        if(((EntityRocketSquid) entity).getSaddled())
        {
            Saddle.render(f5);
            Straps.render(f5);
        }
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
    }
}
