package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
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
            if(stack.getItem() == RocketSquidsBase.SQUELEPORTER_ACTIVE.get()) {
                //The squeleporter is active so squid data is stored.
                stack.getCapability(RocketSquidsBase.SQUELEPORTER_CAP).ifPresent(cap -> {
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
                        newSquid.getCapability(RocketSquidsBase.ADULTCAP).ifPresent(squidCap -> squidCap.loadNBT(cap.getSquidCapabilityData()));
                        worldIn.addFreshEntity(newSquid);
                        if (newSquid.getSaddled()) {
                            playerIn.startRiding(newSquid);
                        }
                        worldIn.playSound(null, playerPos.x, playerPos.y, playerPos.z, Sounds.SQUIDTP_OUT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        //Set the squeleporter to inactive
                        playerIn.setItemInHand(handIn, RocketSquidsBase.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
                        playerIn.getCooldowns().addCooldown(this, 10);
                    });
                });
                return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
