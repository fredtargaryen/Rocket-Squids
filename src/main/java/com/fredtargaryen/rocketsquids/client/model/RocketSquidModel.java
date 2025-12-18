package com.fredtargaryen.rocketsquids.client.model;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;

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
 * and nearly completely re-worked for 1.17.1 on 12-17-2025
 */
public class RocketSquidModel<T extends RocketSquidEntity> extends HierarchicalModel<T> {
    private static final int tenticles = 8;
    public final ModelPart[] tent = new ModelPart[tenticles];
    public final ModelPart saddle;
    public final ModelPart straps;
    public final ModelPart head;

    public RocketSquidModel(ModelPart root) {
        this.head = root;
        this.saddle = this.head.getChild("saddle");
        this.straps = this.head.getChild("straps");
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
                        .texOffs(0, 30)
                        .addBox(-7.0F, -10.0F, -7.0F, 14.0F, 20.0F, 14.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // make the tenticles
        CubeListBuilder tentCubeList = CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 20.0F, 2.0F);

        for (int i = 0; i < tenticles; i++) {
            double doublethink = i * Math.PI * 2.0 / 8.0;
            float floatx = (float)Math.cos(doublethink) * 5.0F;
            float floaty = 9.0F;
            float floatz = (float)Math.sin(doublethink) * 5.0F;
            doublethink = i * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
            root.addOrReplaceChild(createTentacleName(i), tentCubeList, PartPose.offsetAndRotation(floatx, floaty, floatz, 0.0F, (float) doublethink, 0.0F));
        }

        // make the saddle
        root.addOrReplaceChild("saddle",
                CubeListBuilder.create()
                        .texOffs(106, 53)
                        .addBox(-5.0F, 0.0F, 7.0F, 10.0F, 10.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // make the straps
        root.addOrReplaceChild("straps",
                CubeListBuilder.create()
                        .texOffs(68, 0)
                        .addBox(-7.5F, 1.0F, -7.5F, 15.0F, 8.0F, 15.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(meshDef, 128, 64);
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
        this.saddle.visible = entity.getSaddled();
        this.straps.visible = entity.getSaddled();
    }

    @Override
    public @NotNull ModelPart root() {
        return this.head;
    }
}
