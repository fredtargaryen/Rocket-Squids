package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.client.model.*;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
import com.fredtargaryen.rocketsquids.item.ItemNitroInkSac;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRocketSquid.class, new RenderRSFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownSac.class, new RenderSacFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownTube.class, new RenderTubeFactory());
    }

    @Override
    public void registerModels()
    {
        //Describes how items and some blocks should look in the inventory
        ItemModelMesher m = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        m.register(RocketSquidsBase.nitroinksac, 0, new ModelResourceLocation(DataReference.MODID + ":nitroinksac", "inventory"));
        m.register(RocketSquidsBase.turbotube, 0, new ModelResourceLocation(DataReference.MODID + ":turbotube", "inventory"));
    }
}