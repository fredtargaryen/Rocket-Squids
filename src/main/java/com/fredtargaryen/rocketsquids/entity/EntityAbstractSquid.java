package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.World;

public abstract class EntityAbstractSquid extends EntityWaterMob {
    public EntityAbstractSquid(EntityType<?> type, World world) {
        super(type, world);
    }
}
