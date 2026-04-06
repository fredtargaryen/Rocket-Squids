// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render.armor;

import com.fredtargaryen.rocketsquids.client.model.armor.ConchWearableModel;
import com.fredtargaryen.rocketsquids.level.item.ItemConch;
import software.bernie.geckolib.renderer.GeoArmorRenderer;


public final class ConchWearableRenderer extends GeoArmorRenderer<ItemConch> {
    public ConchWearableRenderer() {
        super(new ConchWearableModel());
    }
}
