package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownTubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemTurboTube extends Item {
    public ItemTurboTube() {
        super(new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.abilities.isCreativeMode) {
            stack.grow(-1);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F);
        if (!world.isRemote) {
            ThrownTubeEntity tube = new ThrownTubeEntity(player, world);
            tube.func_213884_b(stack);
            tube.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(tube);
        }

        player.addStat(Stats.ITEM_USED.get(this));
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
    }
}
