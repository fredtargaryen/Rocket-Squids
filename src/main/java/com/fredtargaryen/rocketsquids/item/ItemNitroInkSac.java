package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemNitroInkSac extends Item
{
	public ItemNitroInkSac()
	{
        this.setCreativeTab(CreativeTabs.MATERIALS);
	}

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
     public ActionResult<ItemStack> onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, EnumHand hand)
     {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSound(null, par3EntityPlayer.posX, par3EntityPlayer.posY, par3EntityPlayer.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!par2World.isRemote)
        {
            par2World.spawnEntityInWorld(new EntityThrownSac(par2World, par3EntityPlayer));
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, par1ItemStack);
	}
}