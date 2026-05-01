// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.item.ItemConch;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ConchOnHeadModel extends GeoModel<ItemConch> {
    @Override
    public ResourceLocation getModelResource(ItemConch object) {
        return DataReference.getResourceLocation("geo/armor/conch_armor_model.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ItemConch object) {
        return DataReference.getResourceLocation("textures/models/armor/conch_item_1_layer_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ItemConch animatable) {
        return DataReference.getResourceLocation("animations/armor/conch_armor.animation.json");
    }
}
