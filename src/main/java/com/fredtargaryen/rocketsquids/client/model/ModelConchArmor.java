package com.fredtargaryen.rocketsquids.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

/**
 * Conch - FredTargaryen
 * Created using Tabula 7.0.0
 * <p>
 * Nearly completely re-worked for 1.17.1 on 12-18-2025
 */
public class ModelConchArmor extends HumanoidModel<LivingEntity> {
    public final ModelPart head;
    public final ModelPart hat;

    public ModelPart shape1;
    public ModelPart shape2;
    public ModelPart shape3;
    public ModelPart shape4;

    public ModelConchArmor(ModelPart root, Function<ResourceLocation, RenderType> renderType) {
        super(root, renderType);
        this.head = root.getChild("head");
        this.hat = root.getChild("hat");

        this.shape1 = root.getChild("shape1");
        this.shape2 = root.getChild("shape2");
        this.shape3 = root.getChild("shape3");
        this.shape4 = root.getChild("shape4");
    }

    public static LayerDefinition createArmorLayer(CubeDeformation cubeDeformation, float f) {
        // initial setup
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition root = meshDef.getRoot();

        root.addOrReplaceChild(
                "head", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, cubeDeformation),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );
        root.addOrReplaceChild(
                "hat",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, cubeDeformation.extend(0.5F)),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );

        root.addOrReplaceChild(
                "shape1", CubeListBuilder.create()
                        .texOffs(6, 6)
                        .addBox(-8.0F, -4.0F, 1.0F, 2, 2, 1, cubeDeformation),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );
        root.addOrReplaceChild(
                "shape2", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -4.0F, 0.0F, 2, 1, 1, cubeDeformation),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );
        root.addOrReplaceChild(
                "shape3", CubeListBuilder.create()
                        .texOffs(12, 0)
                        .addBox(-9.0F, -4.0F, 0.0F, 1, 1, 1, cubeDeformation),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );
        root.addOrReplaceChild(
                "shape4", CubeListBuilder.create()
                        .texOffs(5, 2)
                        .addBox(-8.0F, -5.0F, 0.0F, 3, 3, 1, cubeDeformation),
                PartPose.offset(0.0F, 0.0F + f, 0.0F)
        );

        return LayerDefinition.create(meshDef, 16, 16);
    }
}
