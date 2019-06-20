package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.PrimalSquidAEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;

/**
 * PrimalSquidAModel - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class PrimalSquidAModel extends EntityModel<PrimalSquidAEntity> {
    public RendererModel Head;
    public RendererModel BarrelLinkL;
    public RendererModel BarrelLinkR;
    public RendererModel TentMajor1;
    public RendererModel TentMajor2;
    public RendererModel TentMajor3;
    public RendererModel TentMajor4;
    public RendererModel TentMajor5;
    public RendererModel TentMajor6;
    public RendererModel TentMajor7;
    public RendererModel TentMajor8;
    public RendererModel BarrelL;
    public RendererModel BarrelR;
    public RendererModel TentMinor1;
    public RendererModel TentMinor2;
    public RendererModel TentMinor3;
    public RendererModel TentMinor4;
    public RendererModel TentMinor5;
    public RendererModel TentMinor6;
    public RendererModel TentMinor7;
    public RendererModel TentMinor8;

    public PrimalSquidAModel() {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.TentMajor4 = new RendererModel(this, 188, 188);
        this.TentMajor4.setRotationPoint(-8.0F, -4.0F, 0.0F);
        this.TentMajor4.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor4, 0.0F, 0.0F, -2.356194490192345F);
        this.TentMajor8 = new RendererModel(this, 188, 188);
        this.TentMajor8.setRotationPoint(8.0F, -20.0F, 0.0F);
        this.TentMajor8.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor8, 0.0F, 0.0F, 0.7853981633974483F);
        this.TentMinor2 = new RendererModel(this, 188, 222);
        this.TentMinor2.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor2.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.TentMinor5 = new RendererModel(this, 188, 222);
        this.TentMinor5.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor5.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.TentMinor7 = new RendererModel(this, 188, 222);
        this.TentMinor7.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor7.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.TentMajor1 = new RendererModel(this, 188, 188);
        this.TentMajor1.setRotationPoint(0.0F, -21.0F, 0.0F);
        this.TentMajor1.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.TentMinor3 = new RendererModel(this, 188, 222);
        this.TentMinor3.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor3.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.TentMajor2 = new RendererModel(this, 188, 188);
        this.TentMajor2.setRotationPoint(-8.0F, -20.0F, 0.0F);
        this.TentMajor2.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor2, 0.0F, 0.0F, -0.7853981633974483F);
        this.TentMinor4 = new RendererModel(this, 188, 222);
        this.TentMinor4.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor4.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.BarrelLinkL = new RendererModel(this, 0, 84);
        this.BarrelLinkL.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BarrelLinkL.addBox(2.0F, 0.0F, -5.0F, 4, 16, 4, 0.0F);
        this.setRotateAngle(BarrelLinkL, 0.0F, 0.0F, -0.7853981633974483F);
        this.TentMajor5 = new RendererModel(this, 188, 188);
        this.TentMajor5.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.TentMajor5.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor5, 0.0F, 0.0F, -3.141592653589793F);
        this.BarrelLinkR = new RendererModel(this, 0, 84);
        this.BarrelLinkR.mirror = true;
        this.BarrelLinkR.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BarrelLinkR.addBox(-6.0F, 0.0F, -5.0F, 4, 16, 4, 0.0F);
        this.setRotateAngle(BarrelLinkR, 0.0F, 0.0F, 0.7853981633974483F);
        this.TentMajor7 = new RendererModel(this, 188, 188);
        this.TentMajor7.setRotationPoint(9.0F, -12.0F, 0.0F);
        this.TentMajor7.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor7, 0.0F, 0.0F, 1.5707963267948966F);
        this.TentMinor6 = new RendererModel(this, 188, 222);
        this.TentMinor6.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor6.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.BarrelR = new RendererModel(this, 0, 104);
        this.BarrelR.mirror = true;
        this.BarrelR.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BarrelR.addBox(-26.5F, 5.0F, -18.0F, 16, 16, 20, 0.0F);
        this.setRotateAngle(BarrelR, 0.0F, 0.0F, -0.7853981633974483F);
        this.TentMajor3 = new RendererModel(this, 188, 188);
        this.TentMajor3.setRotationPoint(-9.0F, -12.0F, 0.0F);
        this.TentMajor3.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor3, 0.0F, 0.0F, -1.5707963267948966F);
        this.Head = new RendererModel(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-12.0F, -24.0F, -60.0F, 24, 24, 60, 0.0F);
        this.TentMajor6 = new RendererModel(this, 188, 188);
        this.TentMajor6.setRotationPoint(8.0F, -4.0F, 0.0F);
        this.TentMajor6.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 30, 0.0F);
        this.setRotateAngle(TentMajor6, 0.0F, 0.0F, 2.356194490192345F);
        this.BarrelL = new RendererModel(this, 0, 104);
        this.BarrelL.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BarrelL.addBox(10.0F, 5.0F, -18.0F, 16, 16, 20, 0.0F);
        this.setRotateAngle(BarrelL, 0.0F, 0.0F, 0.7853981633974483F);
        this.TentMinor1 = new RendererModel(this, 188, 222);
        this.TentMinor1.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor1.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.TentMinor8 = new RendererModel(this, 188, 222);
        this.TentMinor8.setRotationPoint(0.0F, 0.0F, 29.0F);
        this.TentMinor8.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 30, 0.0F);
        this.Head.addChild(this.TentMajor4);
        this.Head.addChild(this.TentMajor8);
        this.TentMajor2.addChild(this.TentMinor2);
        this.TentMajor5.addChild(this.TentMinor5);
        this.TentMajor7.addChild(this.TentMinor7);
        this.Head.addChild(this.TentMajor1);
        this.TentMajor3.addChild(this.TentMinor3);
        this.Head.addChild(this.TentMajor2);
        this.TentMajor4.addChild(this.TentMinor4);
        this.Head.addChild(this.BarrelLinkL);
        this.Head.addChild(this.TentMajor5);
        this.Head.addChild(this.BarrelLinkR);
        this.Head.addChild(this.TentMajor7);
        this.TentMajor6.addChild(this.TentMinor6);
        this.BarrelLinkR.addChild(this.BarrelR);
        this.Head.addChild(this.TentMajor3);
        this.Head.addChild(this.TentMajor6);
        this.BarrelLinkL.addChild(this.BarrelL);
        this.TentMajor1.addChild(this.TentMinor1);
        this.TentMajor8.addChild(this.TentMinor8);
    }

    @Override
    public void render(PrimalSquidAEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.Head.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(RendererModel modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
