package com.fredtargaryen.rocketsquids.client.render.armor;

import com.fredtargaryen.rocketsquids.client.model.armor.ConchWearableModel;
import com.fredtargaryen.rocketsquids.item.ItemConch;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class ConchWearableRenderer extends GeoArmorRenderer<ItemConch> {
    public ConchWearableRenderer() {
        super(new ConchWearableModel());
        // These values are what each bone name is in blockbench.
        // The default values are the ones that come with the default armor template in the GeckoLib Blockbench plugin.
        this.headBone = "helmet";
        this.bodyBone = "chestplate";
        this.rightArmBone = "rightArm";
        this.leftArmBone = "leftArm";
        this.rightLegBone = "rightLeg";
        this.leftLegBone = "leftLeg";
        this.rightBootBone = "rightBoot";
        this.leftBootBone = "leftBoot";
    }
}
