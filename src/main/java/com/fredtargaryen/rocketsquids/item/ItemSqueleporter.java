package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSqueleporter extends Item {
    public ItemSqueleporter() {
        super(new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB).maxStackSize(1));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if(stack.getItem() == RocketSquidsBase.squeleporter) {
                if(stack.getItemDamage() == 1) {
                    //The squeleporter is active so a squid is stored.
                    EntityRocketSquid ers = stack.getCapability(RocketSquidsBase.SQUELEPORTER, null).getSquid();
                    NBTTagCompound squidTags = new NBTTagCompound();
                    ers.writeEntityToNBT(squidTags);
                    EntityRocketSquid newSquid = (EntityRocketSquid) EntityList.createEntityFromNBT(squidTags, worldIn);
                    if(newSquid != null) {
                        newSquid.forceRotPitch((playerIn.rotationPitch + 90.0F) * Math.PI / 180.0F);
                        newSquid.forceRotYaw((float) (playerIn.getRotationYawHead() * Math.PI / 180.0F));
                        newSquid.addVelocity(playerIn.motionX, playerIn.motionY, playerIn.motionZ);
                        newSquid.addForce(squidTags.getDouble("force"));
                        newSquid.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
                        worldIn.spawnEntity(newSquid);
                        if(newSquid.getSaddled()) {
                            playerIn.startRiding(newSquid);
                        }
                        //Set the squeleporter to inactive
                        stack.setItemDamage(0);
                        playerIn.getCooldownTracker().setCooldown(this, 10);
                        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
