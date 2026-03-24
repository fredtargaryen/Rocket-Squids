// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content.item;

import com.fredtargaryen.rocketsquids.ModRocketSquids;
import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.content.ModItems;
import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
            if(stack.getItem() == ModItems.SQUELEPORTER_ACTIVE.get()) {
                //The squeleporter is active so squid data is stored.
                stack.getCapability(ModRocketSquids.SQUELEPORTER_CAP).ifPresent(cap -> {
                    CompoundTag squidTags = cap.getSquidData();
                    EntityType.create(squidTags, worldIn).ifPresent(entity -> {
                        RocketSquidEntity newSquid = (RocketSquidEntity) entity;
                        newSquid.forceRotPitch((playerIn.getXRot() + 90.0F) * Math.PI / 180.0F);
                        newSquid.forceRotYaw((float) (playerIn.getYHeadRot() * Math.PI / 180.0F));
                        Vec3 playerMotion = playerIn.getDeltaMovement();
                        newSquid.push(playerMotion.x, playerMotion.y, playerMotion.z);
                        newSquid.addForce(squidTags.getDouble("force"));
                        Vec3 playerPos = playerIn.position();
                        newSquid.setPos(playerPos.x, playerPos.y, playerPos.z);
                        newSquid.getCapability(ModRocketSquids.ADULTCAP).ifPresent(squidCap -> squidCap.loadNBT(cap.getSquidCapabilityData()));
                        worldIn.addFreshEntity(newSquid);
                        if (newSquid.getSaddled()) {
                            playerIn.startRiding(newSquid);
                        }
                        worldIn.playSound(null, playerPos.x, playerPos.y, playerPos.z, ModSounds.SQUIDTP_OUT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        //Set the squeleporter to inactive
                        playerIn.setItemInHand(handIn, ModItems.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
                        playerIn.getCooldowns().addCooldown(this, 10);
                    });
                });
                return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
