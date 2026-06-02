// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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
    protected boolean canRide(@NotNull Entity entityIn)
    {
        return true;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isLeashed();
    }

    @SuppressWarnings("unused")
    public abstract boolean canBeRiddenUnderFluidType(Entity rider);
}
