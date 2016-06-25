package com.fredtargaryen.rocketsquids.proxy;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.client.model.RenderRSFactory;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquid;
import com.fredtargaryen.rocketsquids.client.model.RenderRS;
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
    }

    @Override
    public void registerModels()
    {
        //Describes how items and some blocks should look in the inventory
        ItemModelMesher m = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        m.register(RocketSquidsBase.nitroinksac, 0, new ModelResourceLocation(DataReference.MODID + ":nitroinksac"));
    }
}