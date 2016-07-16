package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderSacFactory implements IRenderFactory<EntityThrownSac>
{
    private static final ResourceLocation sactex = new ResourceLocation(DataReference.MODID + ":textures/items/nitroinksac.png");

    @Override
    public Render<? super EntityThrownSac> createRenderFor(RenderManager manager)
    {
        return new RenderSnowball<EntityThrownSac>(manager, RocketSquidsBase.nitroinksac, Minecraft.getMinecraft().getRenderItem());
    }
}
