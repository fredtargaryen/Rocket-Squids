package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class AbstractSquidEntity extends WaterMobEntity {
    protected boolean newPacketRequired;

    ///////////////
    //CLIENT ONLY//
    ///////////////
    public float tentacleAngle;
    public float lastTentacleAngle;

    public AbstractSquidEntity(EntityType<? extends WaterMobEntity> type, World world) {
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
