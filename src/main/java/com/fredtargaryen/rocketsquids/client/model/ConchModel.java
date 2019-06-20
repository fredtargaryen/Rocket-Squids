package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 */
public class ConchModel extends BipedModel {
    public static final ResourceLocation CONCH_TEX = new ResourceLocation(DataReference.MODID + ":textures/armour/conch.png");

    public RendererModel shape1;
    public RendererModel shape2;
    public RendererModel shape3;
    public RendererModel shape6;

    public ConchModel() {
        super();
        this.textureWidth = 16;
        this.textureHeight = 16;
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
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Minecraft.getInstance().getTextureManager().bindTexture(CONCH_TEX);
        this.shape2.render(f5);
        this.shape6.render(f5);
        this.shape3.render(f5);
        this.shape1.render(f5);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void getHeadAngles(RenderPlayerEvent.Pre rle) {
        RendererModel head = rle.getRenderer().func_217764_d().field_78116_c; //The head model... probably
        copyHeadAngles(head, this.shape2);
        copyHeadAngles(head, this.shape6);
        copyHeadAngles(head, this.shape3);
        copyHeadAngles(head, this.shape1);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void copyHeadAngles(RendererModel head, RendererModel conchPiece) {
        conchPiece.rotateAngleX = head.rotateAngleX;
        conchPiece.rotateAngleY = head.rotateAngleY;
        conchPiece.rotateAngleZ = head.rotateAngleZ;
    }
}
