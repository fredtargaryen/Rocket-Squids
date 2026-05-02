// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.item.ConchItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ConchOnHeadModel extends GeoModel<ConchItem> {
    @Override
    public Identifier getModelResource(ConchItem object) {
        return DataReference.getIdentifier("geo/armor/conch_armor_model.geo.json");
    }

    @Override
    public Identifier getTextureResource(ConchItem object) {
        return DataReference.getIdentifier("textures/models/armor/conch_item_1_layer_1.png");
    }

    @Override
    public Identifier getAnimationResource(ConchItem animatable) {
        return DataReference.getIdentifier("animations/armor/conch_armor.animation.json");
    }
}
