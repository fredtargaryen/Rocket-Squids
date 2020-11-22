package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownSacEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemNitroInkSac extends Item {
    public ItemNitroInkSac() {
        super(new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isCreative()) {
            stack.grow(-1);
        }
        Vector3d pos = player.getPositionVec();
        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F);
        if (!world.isRemote) {
            ThrownSacEntity sac = new ThrownSacEntity(player, world);
            sac.shoot(pos.x, pos.y, pos.z, 0.0F, 1.5F);
            world.addEntity(sac);
        }
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
	}
}