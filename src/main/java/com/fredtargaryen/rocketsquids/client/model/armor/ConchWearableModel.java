package com.fredtargaryen.rocketsquids.client.model.armor;

import com.fredtargaryen.rocketsquids.content.item.ItemConch;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@SuppressWarnings("removal")
public class ConchWearableModel extends GeoModel<ItemConch> {
    @Override
    public ResourceLocation getModelResource(ItemConch object) {
        return new ResourceLocation(MODID, "geo/armor/conch_armor_model.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ItemConch object) {
        return new ResourceLocation(MODID, "textures/models/armor/conch_armor_model.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ItemConch animatable) {
        return new ResourceLocation(MODID, "animations/armor/conch_armor.animation.json");
    }
}
