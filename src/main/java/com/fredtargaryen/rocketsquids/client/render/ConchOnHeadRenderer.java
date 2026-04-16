// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.client.model.ConchOnHeadModel;
import com.fredtargaryen.rocketsquids.level.item.ItemConch;
import software.bernie.geckolib.renderer.GeoArmorRenderer;


public final class ConchOnHeadRenderer extends GeoArmorRenderer<ItemConch> {
    public ConchOnHeadRenderer() {
        super(new ConchOnHeadModel());
    }
}
