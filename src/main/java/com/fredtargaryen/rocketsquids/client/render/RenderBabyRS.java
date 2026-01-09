package com.fredtargaryen.rocketsquids.client.render;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquidBaby;
import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.client.event.ModEventClient.BABY_SQUID_BODY_LAYER;

public class RenderBabyRS extends MobRenderer<BabyRocketSquidEntity, ModelRocketSquidBaby<BabyRocketSquidEntity>> {
    private static final ResourceLocation normal = new ResourceLocation(DataReference.MODID + ":textures/entity/baby_rocket_squid.png");

    public RenderBabyRS(
            EntityRendererProvider.Context context
    ) {
        super(context, new ModelRocketSquidBaby<>(context.bakeLayer(BABY_SQUID_BODY_LAYER)), 1.0f);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     * par2 = time elapsed since last render call
     */
    @Override
    protected float getBob(
            BabyRocketSquidEntity squid,
            float partialTicks
    ) {
        return squid.lastTentacleAngle + (squid.tentacleAngle - squid.lastTentacleAngle) * partialTicks;
    }

    @Override
    protected void setupRotations(
            BabyRocketSquidEntity ers,
            PoseStack matrixStack,
            float ageInTicks,
            float rotationYaw,
            float partialTicks
    ) {
        float exactPitch = (float) (Mth.lerp(partialTicks, ers.getPrevRotPitch(), ers.getRotPitch()) * 180 / Math.PI);
        float exactYaw = (float) (Mth.lerp(partialTicks, ers.getPrevRotYaw(), ers.getRotYaw()) * 180 / Math.PI);
        //0.5F for adults
        matrixStack.translate(0, 0.15, 0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180f - exactYaw));
        matrixStack.mulPose(Vector3f.XN.rotationDegrees(exactPitch));
        //1.2F for adults
        matrixStack.translate(0f, -1.3f, 0f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BabyRocketSquidEntity entity) {
        return normal;
    }
}
