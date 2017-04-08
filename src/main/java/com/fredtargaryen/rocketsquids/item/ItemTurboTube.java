package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.entity.EntityThrownTube;
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

public class ItemTurboTube extends Item
{
    public ItemTurboTube()
    {
        super();
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (!player.capabilities.isCreativeMode)
        {
            --stack.stackSize;
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote)
        {
            EntityThrownTube tube = new EntityThrownTube(world, player);
            tube.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntityInWorld(tube);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
