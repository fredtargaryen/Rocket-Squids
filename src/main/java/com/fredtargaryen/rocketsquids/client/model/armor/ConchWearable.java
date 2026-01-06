package com.fredtargaryen.rocketsquids.client.model.armor;

import com.fredtargaryen.rocketsquids.item.GeoModArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ConchWearable extends AnimatedGeoModel<GeoModArmorItem> {
    @Override
    public ResourceLocation getModelLocation(GeoModArmorItem object) {
        return new ResourceLocation(MODID, ":geo/armor/conch_armor_model.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(GeoModArmorItem object) {
        return new ResourceLocation(MODID, ":textures/models/armor/conch_armor_model.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GeoModArmorItem animatable) {
        return new ResourceLocation(MODID, ":animations/armor/armor.animation.json");
    }
}
