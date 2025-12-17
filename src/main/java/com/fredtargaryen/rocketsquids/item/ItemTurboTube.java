package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.projectile.ThrownTubeEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemTurboTube extends Item {
    public ItemTurboTube(Item.Properties properties) {
        super(properties);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level world,
            Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.grow(-1);
        }
        Vec3 pos = player.position();
        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F);
        if (!world.isClientSide) {
            ThrownTubeEntity tube = new ThrownTubeEntity(player, world);
            tube.setItem(stack);
            Vector3f aimPos = RocketSquidsBase.getPlayerAimVector(player);
            tube.shoot(aimPos.x(), aimPos.y(), aimPos.z(), 1.5F, 1.0F);
            world.addFreshEntity(tube);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
