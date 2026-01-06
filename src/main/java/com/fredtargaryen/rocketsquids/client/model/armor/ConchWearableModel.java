package com.fredtargaryen.rocketsquids.client.model.armor;

import com.fredtargaryen.rocketsquids.item.ItemConch;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ConchWearableModel extends AnimatedGeoModel<ItemConch> {
    @Override
    public ResourceLocation getModelLocation(ItemConch object) {
        return new ResourceLocation(MODID, ":geo/armor/conch_armor_model.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ItemConch object) {
        return new ResourceLocation(MODID, ":textures/models/armor/conch_armor_model.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ItemConch animatable) {
        return new ResourceLocation(MODID, ":animations/armor/armor.animation.json");
    }
}
