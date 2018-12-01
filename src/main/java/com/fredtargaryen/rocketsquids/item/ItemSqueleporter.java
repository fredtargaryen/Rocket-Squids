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
        super();
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
                    Entity newSquid = EntityList.createEntityFromNBT(squidTags, worldIn);
                    if(newSquid != null) {
                        worldIn.spawnEntity(newSquid);
                        //TODO If the squid is saddled start riding it
                        stack.setItemDamage(0);
                        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
