package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class ItemSqueleporter extends Item {
    public ItemSqueleporter(Item.Properties props) {
        super(props.maxStackSize(1));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if(stack.getItem() == RocketSquidsBase.SQUELEPORTER_ACTIVE) {
                //The squeleporter is active so a squid is stored.
                stack.getCapability(RocketSquidsBase.SQUELEPORTER_CAP).ifPresent(cap -> {
                    RocketSquidEntity ers = cap.getSquid();
                    ers.removed = false;
                    CompoundNBT squidTags = new CompoundNBT();
                    ers.writeUnlessRemoved(squidTags);
                    EntityType.func_220330_a(squidTags, worldIn).ifPresent(entity -> {
                        RocketSquidEntity newSquid = (RocketSquidEntity) entity;
                        newSquid.forceRotPitch((playerIn.rotationPitch + 90.0F) * Math.PI / 180.0F);
                        newSquid.forceRotYaw((float) (playerIn.getRotationYawHead() * Math.PI / 180.0F));
                        Vec3d playerMotion = playerIn.getMotion();
                        newSquid.addVelocity(playerMotion.x, playerMotion.y, playerMotion.z);
                        newSquid.addForce(squidTags.getDouble("force"));
                        newSquid.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
                        worldIn.func_217376_c(newSquid);
                        if (newSquid.getSaddled()) {
                            playerIn.startRiding(newSquid);
                        }
                        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, Sounds.SQUIDTP_OUT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        //Set the squeleporter to inactive
                        playerIn.setHeldItem(handIn, RocketSquidsBase.SQUELEPORTER_INACTIVE.getDefaultInstance());
                        playerIn.getCooldownTracker().setCooldown(this, 10);
                    });
                });
                return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
