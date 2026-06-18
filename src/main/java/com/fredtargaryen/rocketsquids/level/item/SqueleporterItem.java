// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.item;

import com.fredtargaryen.rocketsquids.RSItems;
import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import com.fredtargaryen.rocketsquids.util.ValueIOHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.fredtargaryen.rocketsquids.RSDataComponentTypes.SQUELEPORTER;

public class SqueleporterItem extends Item {
    public SqueleporterItem(Item.Properties properties) {
        super(properties);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public @NotNull InteractionResult use(
            Level level,
            @NotNull Player playerIn,
            @NotNull InteractionHand handIn
    ) {
        if (!level.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.getItem() == RSItems.SQUELEPORTER_ACTIVE.get()) {
                //The squeleporter is active so squid data is stored.
                SqueleporterData data = stack.get(SQUELEPORTER);
                ValueInput squidVi = ValueIOHelper.getCompoundTagAsValueInput(data.squidData());
                EntityType.create(squidVi, level, EntitySpawnReason.MOB_SUMMONED).ifPresent(entity -> {
                    RocketSquidEntity newSquid = (RocketSquidEntity) entity;
                    newSquid.forcePitchInstant((playerIn.getXRot() + 90.0F) * RotationHelper.DEG2RAD);
                    newSquid.forceYawInstant((float) (playerIn.getYHeadRot() * RotationHelper.DEG2RAD));
                    RotationHelper.moveSquidInDirectionPointing(newSquid);
                    Vec3 playerMotion = playerIn.getDeltaMovement();
                    newSquid.push(playerMotion.x, playerMotion.y, playerMotion.z);
                    Vec3 playerPos = playerIn.position();
                    if (newSquid.getSaddled()) {
                        newSquid.setPos(playerPos.x, playerPos.y, playerPos.z);
                        level.addFreshEntity(newSquid);
                        playerIn.startRiding(newSquid);
                        level.playSound(null, playerPos.x, playerPos.y, playerPos.z, RSSounds.SQUIDTP_OUT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        //Set the squeleporter to inactive
                        playerIn.setItemInHand(handIn, RSItems.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
                        playerIn.getCooldowns().addCooldown(stack, 10);
                    }
                    else {
                        Vec3 direction = RotationHelper.getSquidDirection(newSquid);
                        double spawnDistance = 3.0;
                        boolean canSpawn = false;
                        Vec3 spawnPos = playerIn.getEyePosition();
                        while (!canSpawn && spawnDistance >= 0.0) {
                            spawnPos = playerIn.getEyePosition().add(direction.scale(spawnDistance));
                            BlockPos spawnBlockPos = BlockPos.containing(spawnPos.x, spawnPos.y, spawnPos.z);
                            if (level.getBlockState(spawnBlockPos).isSolid()) {
                                spawnDistance -= 1.0;
                            }
                            else {
                                canSpawn = true;
                            }
                        }
                        if (canSpawn) {
                            newSquid.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                            level.addFreshEntity(newSquid);
                            level.playSound(null, playerPos.x, playerPos.y, playerPos.z, RSSounds.SQUIDTP_OUT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            //Set the squeleporter to inactive
                            playerIn.setItemInHand(handIn, RSItems.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
                            playerIn.getCooldowns().addCooldown(stack, 10);
                        }
                    }
                });
            }
            return InteractionResult.PASS;
        }
        return super.use(level, playerIn, handIn);
    }
}
