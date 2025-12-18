package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * RocketSquidModel - FredTargaryen
 * Created using Tabula 7.0.0
 * <p>
 * Further manually edited by barnabeepickle on 12-4-2025,
 * and nearly completely re-worked for 1.17.1 on 12-18-2025
 */
public class ModelRocketSquidBaby<T extends BabyRocketSquidEntity> extends HierarchicalModel<T> {
    private static final int tenticles = 8;
    public final ModelPart[] tent = new ModelPart[tenticles];
    public final ModelPart head;

    public ModelRocketSquidBaby(ModelPart root) {
        this.head = root;
        Arrays.setAll(this.tent, index -> head.getChild(createTentacleName(index)));
    }

    private static String createTentacleName(int index) {
        return "tent" + index;
    }

    public static LayerDefinition createBodyLayer() {
        // initial setup
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition root = meshDef.getRoot();

        // making the head/body
        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(-3.0F, -3.0F, -2.0F, 5.0F, 7.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // make the tenticles
        CubeListBuilder tentCubeList = CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F);

        for (int i = 0; i < tenticles; i++) {
            double doublethink = i * Math.PI * 2.0 / 8.0;
            float floatx = (float)Math.cos(doublethink) * 1.5F;
            float floaty = 4.0F;
            float floatz = (float)Math.sin(doublethink) * 1.5F;
            doublethink = i * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
            root.addOrReplaceChild(createTentacleName(i), tentCubeList, PartPose.offsetAndRotation(floatx, floaty, floatz, 0.0F, (float) doublethink, 0.0F));
        }

        return LayerDefinition.create(meshDef, 32, 32);
    }


    @Override
    public void setupAnim(
            @NotNull T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        for (ModelPart modelPart : this.tent) {
            modelPart.xRot = ageInTicks;
        }
    }

    @Override
    public @NotNull ModelPart root() {
        return this.head;
    }
}
