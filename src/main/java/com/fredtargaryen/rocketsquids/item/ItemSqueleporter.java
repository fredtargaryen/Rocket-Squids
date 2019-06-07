package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemSqueleporter extends Item {
    public ItemSqueleporter(Item.Properties props) {
        super(props.maxStackSize(1));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if(stack.getItem() == RocketSquidsBase.SQUELEPORTER_ACTIVE) {
                //The squeleporter is active so a squid is stored.
                stack.getCapability(RocketSquidsBase.SQUELEPORTER_CAP).ifPresent(cap -> {
                    EntityRocketSquid ers = cap.getSquid();
                    ers.removed = false;
                    NBTTagCompound squidTags = new NBTTagCompound();
                    ers.writeUnlessRemoved(squidTags);
                    EntityRocketSquid newSquid = (EntityRocketSquid) EntityType.create(squidTags, worldIn);
                    if (newSquid != null) {
                        newSquid.forceRotPitch((playerIn.rotationPitch + 90.0F) * Math.PI / 180.0F);
                        newSquid.forceRotYaw((float) (playerIn.getRotationYawHead() * Math.PI / 180.0F));
                        newSquid.addVelocity(playerIn.motionX, playerIn.motionY, playerIn.motionZ);
                        newSquid.addForce(squidTags.getDouble("force"));
                        newSquid.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
                        worldIn.spawnEntity(newSquid);
                        if (newSquid.getSaddled()) {
                            playerIn.startRiding(newSquid);
                        }
                        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, Sounds.SQUIDTP_OUT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        //Set the squeleporter to inactive
                        playerIn.setHeldItem(handIn, RocketSquidsBase.SQUELEPORTER_INACTIVE.getDefaultInstance());
                        playerIn.getCooldownTracker().setCooldown(this, 10);
                    }
                });
                return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
