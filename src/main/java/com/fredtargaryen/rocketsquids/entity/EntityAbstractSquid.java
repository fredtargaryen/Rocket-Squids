package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class EntityAbstractSquid extends EntityWaterMob {
    protected boolean newPacketRequired;

    ///////////////
    //CLIENT ONLY//
    ///////////////
    public float tentacleAngle;
    public float lastTentacleAngle;

    public EntityAbstractSquid(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource ds)
    {
        return null;
    }

    /**
     * Returns the sound this mob makes when it dies.
     */
    @Override
    protected SoundEvent getDeathSound()
    {
        return null;
    }
}
