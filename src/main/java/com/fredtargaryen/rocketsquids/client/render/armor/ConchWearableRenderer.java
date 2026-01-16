package com.fredtargaryen.rocketsquids.client.render.armor;

import com.fredtargaryen.rocketsquids.client.model.armor.ConchWearableModel;
import com.fredtargaryen.rocketsquids.item.ItemConch;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class ConchWearableRenderer extends GeoArmorRenderer<ItemConch> {
    public ConchWearableRenderer() {
        super(new ConchWearableModel());
        // These values are what each bone name is in blockbench.
        // The default values are the ones that come with the default armor template in the GeckoLib Blockbench plugin.
        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.leftLegBone = "armorLeftLeg";
        this.leftBootBone = "armorLeftBoot";
        this.rightLegBone = "armorRightLeg";
        this.rightBootBone = "armorRightBoot";
    }
}
