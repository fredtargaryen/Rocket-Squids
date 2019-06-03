package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
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
public class ModelConch extends ModelBiped {
    public static final ResourceLocation CONCH_TEX = new ResourceLocation(DataReference.MODID + ":textures/armour/conch.png");

    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape6;

    public ModelConch() {
        super();
        this.textureWidth = 16;
        this.textureHeight = 16;
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
        ModelRenderer head = rle.getRenderer().getMainModel().bipedHead;
        copyModelAngles(head, this.shape2);
        copyModelAngles(head, this.shape6);
        copyModelAngles(head, this.shape3);
        copyModelAngles(head, this.shape1);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
