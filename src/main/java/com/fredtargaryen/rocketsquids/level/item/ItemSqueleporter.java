// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.item;

import com.fredtargaryen.rocketsquids.RSItems;
import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.level.attachment.RocketSquidData;
import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.RSAttachmentTypes.SQUID;
import static com.fredtargaryen.rocketsquids.RSDataComponentTypes.SQUELEPORTER;

public class ItemSqueleporter extends Item {
    public ItemSqueleporter(Item.Properties properties) {
        super(properties);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level worldIn,
            @NotNull Player playerIn,
            @NotNull InteractionHand handIn
    ) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.getItem() == RSItems.SQUELEPORTER_ACTIVE.get()) {
                //The squeleporter is active so squid data is stored.
                SqueleporterData data = stack.get(SQUELEPORTER);
                CompoundTag squidTags = data.entityData();
                EntityType.create(squidTags, worldIn).ifPresent(entity -> {
                    RocketSquidEntity newSquid = (RocketSquidEntity) entity;
                    RocketSquidData newSquidData = new RocketSquidData();
                    newSquidData.deserializeNBT(null, data.attachmentData());
                    newSquid.setData(SQUID, newSquidData);
                    newSquid.forceRotPitch((playerIn.getXRot() + 90.0F) * Math.PI / 180.0F);
                    newSquid.forceRotYaw((float) (playerIn.getYHeadRot() * Math.PI / 180.0F));
                    Vec3 playerMotion = playerIn.getDeltaMovement();
                    newSquid.push(playerMotion.x, playerMotion.y, playerMotion.z);
                    newSquid.addForce(squidTags.getDouble("force"));
                    Vec3 playerPos = playerIn.position();
                    newSquid.setPos(playerPos.x, playerPos.y, playerPos.z);
                    worldIn.addFreshEntity(newSquid);
                    if (newSquid.getSaddled()) {
                        playerIn.startRiding(newSquid);
                    }
                    worldIn.playSound(null, playerPos.x, playerPos.y, playerPos.z, RSSounds.SQUIDTP_OUT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    //Set the squeleporter to inactive
                    playerIn.setItemInHand(handIn, RSItems.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
                    playerIn.getCooldowns().addCooldown(this, 10);
                });
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
