package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.EntityThrownSac;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemNitroInkSac extends Item {
    public ItemNitroInkSac() {
        super(new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
     public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
     {
         ItemStack stack = player.getHeldItem(hand);
        if (!player.isCreative())
        {
            stack.grow(-1);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F);
        if (!world.isRemote)
        {
            EntityThrownSac sac = new EntityThrownSac(player, world);
            sac.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(sac);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
}