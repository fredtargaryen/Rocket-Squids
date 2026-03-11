package com.fredtargaryen.rocketsquids.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.client.event.ModClientHandler.SQUAVIGATOR_IN_HAND;

public class SquavigatorBakedModelWrapper extends BakedModelWrapper<BakedModel> {
    public SquavigatorBakedModelWrapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull BakedModel applyTransform(
            @NotNull ItemDisplayContext context,
            @NotNull PoseStack stack,
            boolean applyLeftHandTransform
    ) {
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            return Minecraft.getInstance().getModelManager().getModel(SQUAVIGATOR_IN_HAND).applyTransform(context, stack, applyLeftHandTransform);
        }
        return originalModel.applyTransform(context, stack, applyLeftHandTransform);
    }

}
