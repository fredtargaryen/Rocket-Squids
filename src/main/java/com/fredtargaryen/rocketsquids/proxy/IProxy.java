package com.fredtargaryen.rocketsquids.proxy;

import net.minecraft.client.model.HumanoidModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public interface IProxy
{
    void clientSetup(FMLClientSetupEvent event);

    void registerRenderers(EntityRenderersEvent.RegisterRenderers event);

    void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event);

    void registerRenderTypes();

    void openConchClient(byte conchStage);

    void playNoteFromMessage(byte note);

    void playNoteFromMessageConchNeeded(byte note);
}
