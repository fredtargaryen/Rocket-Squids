// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity;

import com.fredtargaryen.rocketsquids.RSEntities;
import com.fredtargaryen.rocketsquids.RSEntityDataSerializers;
import com.fredtargaryen.rocketsquids.level.entity.ai.BabyFlopAroundGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.BabySwimAroundGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.fredtargaryen.rocketsquids.DataReference.DOUBLE_PI;

public class BabyRocketSquidEntity extends AbstractRocketSquidEntity {
    //Properties controlled by the server, but which have a visual effect so need to be synced to clients
    private static final EntityDataAccessor<Double> PITCH_PREV = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH_TARGET = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Double> YAW_PREV = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW_TARGET = SynchedEntityData.defineId(BabyRocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    public BabyRocketSquidEntity(EntityType<? extends BabyRocketSquidEntity> type, Level w) {
        super(type, w);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PITCH_PREV, 0.0);
        this.entityData.define(PITCH, 0.0);
        this.entityData.define(PITCH_TARGET, 0.0);
        this.entityData.define(YAW_PREV, 0.0);
        this.entityData.define(YAW, 0.0);
        this.entityData.define(YAW_TARGET, 0.0);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BabySwimAroundGoal(this, 0.15));
        this.goalSelector.addGoal(1, new BabyFlopAroundGoal(this));
        //this.goalSelector.addGoal(2, new BabyRSFollowParentGoal(this, 1.0));
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn) {
        return false;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(Entity rider) {
        return false;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.tickCount > 72000) {
            if (!this.level().isClientSide()) {
                this.remove(RemovalReason.DISCARDED);
                RocketSquidEntity adult = new RocketSquidEntity(this.level());
                Vec3 pos = this.position();
                adult.moveTo(pos.x, pos.y, pos.z);
                adult.forceRotPitch(this.getEntityData().get(PITCH));
                adult.setTargetRotPitch(this.getEntityData().get(PITCH_TARGET));
                adult.forceRotYaw(this.getEntityData().get(YAW));
                adult.setTargetRotYaw(this.getEntityData().get(YAW_TARGET));
                this.level().addFreshEntity(adult);
            }
        } else {
            //Do on client and server
            //Fraction of distance to target rotation to rotate by each server tick
            double rotateSpeed;
            if (this.wasTouchingWater) {
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9);
                rotateSpeed = 0.06;
            } else {
                Vec3 oldMotion = this.getDeltaMovement();
                double motionX = oldMotion.x;
                double motionY = oldMotion.y;
                double motionZ = oldMotion.z;
                if (this.hurtTime > 0) {
                    motionX = 0.0D;
                    motionZ = 0.0D;
                }
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    motionY += 0.05D * (double) (Objects.requireNonNull(this.getEffect(MobEffects.LEVITATION)).getAmplifier() + 1) - motionY;
                } else if (!this.isNoGravity()) {
                    motionY -= 0.08D;
                }
                motionX *= 0.9800000190734863D;
                motionY *= 0.9800000190734863D;
                motionZ *= 0.9800000190734863D;
                rotateSpeed = 0.15;
                this.setDeltaMovement(motionX, motionY, motionZ);
            }

            //Rotate towards target pitch
            double trp = this.getEntityData().get(PITCH_TARGET);
            double rp = this.getEntityData().get(PITCH);
            if (trp != rp) {
                //Squids rotate <= 180 degrees either way.
                //The squid can rotate out of the interval [-PI, PI].
                rp += (trp - rp) * rotateSpeed;
                this.setPitch(rp);
            }

            //Rotate towards target yaw
            double trY = this.getEntityData().get(YAW_TARGET);
            double ry = this.getEntityData().get(YAW);
            if (trY != ry) {
                ry += (trY - ry) * rotateSpeed;
                this.setYaw(ry);
            }

            if (this.level().isClientSide()) {
                //Client side
                //Handles tentacle angles
                this.lastTentacleAngle = this.tentacleAngle;
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.wasTouchingWater ? (float) ((Math.PI / 6) + (Mth.sin((float) Math.toRadians(4 * (this.tickCount % 360))) * Math.PI / 6)) : 0;
            } else {
                //Server side
                if (this.isInWater()) {
                    this.moveToWherePointing();
                }
            }
        }
    }

    @Override
    public boolean isBaby() {
        return true;
    }

    @SuppressWarnings("unused")
    public void spawnHearts(ServerLevel level) {
        Vec3 thisPos = this.position();
        level.sendParticles(ParticleTypes.HEART.getType(), thisPos.x, thisPos.y + 1.5D, thisPos.z, 3, 0.25D, 0.0D, 0.25D, 1.5D);
    }

    /**
     * Entity won't drop experience orbs if this returns false
     */
    @Override
    public boolean shouldDropExperience() {
        // since this is a baby we don't want it to drop xp
        return false;
    }

    /**
     * Entity won't drop items if this returns false
     */
    @Override
    protected boolean shouldDropLoot() {
        // since this is a baby we don't want it to drop items
        return false;
    }

    public double getPrevRotPitch() {
        return this.getEntityData().get(PITCH_PREV);
    }

    public double getPrevRotYaw() {
        return this.getEntityData().get(YAW_PREV);
    }

    public double getRotPitch() {
        return this.getEntityData().get(PITCH);
    }

    public void setPitch(double p) {
        this.getEntityData().set(PITCH_PREV, this.getEntityData().get(PITCH));
        this.getEntityData().set(PITCH, p);
    }

    public double getRotYaw() {
        return this.getEntityData().get(YAW);
    }

    public void setYaw(double y) {
        this.getEntityData().set(YAW_PREV, this.getEntityData().get(YAW));
        this.getEntityData().set(YAW, y);
    }

    public void setTargetRotPitch(double d) {
        double currentPitch = this.getEntityData().get(PITCH);
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while(currentPitch < -Math.PI) {
            currentPitch += DOUBLE_PI;
        }
        while(d < -Math.PI) {
            d += DOUBLE_PI;
        }
        while(currentPitch > Math.PI) {
            currentPitch -= DOUBLE_PI;
        }
        while(d > Math.PI) {
            d -= DOUBLE_PI;
        }
        this.setPitch(currentPitch);
        this.getEntityData().set(PITCH_TARGET, d);
    }

    public void setTargetRotYaw(double d) {
        double currentYaw = this.getEntityData().get(YAW);
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while(currentYaw < -Math.PI) {
            currentYaw += DOUBLE_PI;
        }
        while(d < -Math.PI) {
            d += DOUBLE_PI;
        }
        while(currentYaw > Math.PI) {
            currentYaw -= DOUBLE_PI;
        }
        while(d > Math.PI) {
            d -= DOUBLE_PI;
        }
        this.setYaw(currentYaw);
        this.getEntityData().set(YAW_TARGET, d);
    }

    public double getTargRotPitch() {
        return this.getEntityData().get(PITCH_TARGET);
    }

    public double getTargRotYaw() {
        return this.getEntityData().get(YAW_TARGET);
    }

    ///////
    //NBT//
    ///////
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("id", RSEntities.BABY_SQUID_TYPE.toString());
        Vec3 motion = this.getDeltaMovement();
        compound.putDouble("force", Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z));
        compound.putDouble("pitch", this.getRotPitch());
        compound.putDouble("yaw", this.getRotYaw());
        compound.putDouble("targetPitch", this.getTargRotPitch());
        compound.putDouble("targetYaw", this.getTargRotYaw());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        //Force comes from reading motion tags
        this.getEntityData().set(PITCH, compound.getDouble("pitch"));
        this.getEntityData().set(YAW, compound.getDouble("yaw"));
        this.setTargetRotPitch(compound.getDouble("targetPitch"));
        this.setTargetRotYaw(compound.getDouble("targetYaw"));
    }
}
