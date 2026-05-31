// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * RocketSquidModel - FredTargaryen
 * Created using Tabula 7.0.0
 * <p>
 * Further manually edited by barnabeepickle on 12-4-2025,
 * and nearly completely re-worked for 1.17.1 on 12-18-2025
 */
public class BabyRocketSquidModel extends RocketSquidModel {
    public BabyRocketSquidModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        // initial setup
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition root = meshDef.getRoot();

        // make the head/body
        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(-3.0F, -3.0F, -2.0F, 5.0F, 7.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // make the tentacles
        CubeListBuilder tentCubeList = CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F);

        for (int i = 0; i < tentacles; i++) {
            double tentacleYRot = i * Math.PI * 2.0 / 8.0;
            float floatx = (float) Math.cos(tentacleYRot) * 1.5F;
            float floaty = 4.0F;
            float floatz = (float) Math.sin(tentacleYRot) * 1.5F;
            tentacleYRot = i * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
            root.addOrReplaceChild(createTentacleName(i), tentCubeList, PartPose.offsetAndRotation(floatx, floaty, floatz, 0.0F, (float) tentacleYRot, 0.0F));
        }

        // Baby squids can't be ridden so have invisible saddles and straps forever
        // make the saddle
        root.addOrReplaceChild("saddle",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // make the straps
        root.addOrReplaceChild("straps",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(meshDef, 32, 32);
    }
}
