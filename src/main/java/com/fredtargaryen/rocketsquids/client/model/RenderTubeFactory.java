package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTubeFactory implements IRenderFactory<EntityThrownTube>
{
    private static final ResourceLocation tubetex = new ResourceLocation(DataReference.MODID + ":textures/items/turbotube.png");

    @Override
    public Render<? super EntityThrownTube> createRenderFor(RenderManager manager) {
            return new RenderSnowball<EntityThrownTube>(manager, RocketSquidsBase.turbotube, Minecraft.getMinecraft().getRenderItem());
    }
}
