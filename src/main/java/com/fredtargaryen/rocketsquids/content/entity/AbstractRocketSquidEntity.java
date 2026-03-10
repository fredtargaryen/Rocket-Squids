package com.fredtargaryen.rocketsquids.content.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRocketSquidEntity extends AbstractSquidEntity{
    public AbstractRocketSquidEntity(EntityType<? extends AbstractSquidEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem == Items.COD || stackItem == Items.SALMON || stackItem == Items.TROPICAL_FISH || stackItem == Items.GUNPOWDER;
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn)
    {
        return true;
    }

    public abstract boolean canBeRiddenUnderFluidType(Entity rider);
}
