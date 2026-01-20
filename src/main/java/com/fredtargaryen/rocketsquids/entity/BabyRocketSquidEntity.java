package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.cap.entity.baby.BabyCap;
import com.fredtargaryen.rocketsquids.entity.ai.BabyFlopAroundGoal;
import com.fredtargaryen.rocketsquids.entity.ai.BabySwimAroundGoal;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageBabyCapData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BabyRocketSquidEntity extends AbstractRocketSquidEntity {
    private BabyCap squidCap;

    public BabyRocketSquidEntity(EntityType<? extends BabyRocketSquidEntity> type, Level w) {
        super(type, w);
        this.getCapability(RocketSquidsBase.BABYCAP).ifPresent(cap -> BabyRocketSquidEntity.this.squidCap = cap);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BabySwimAroundGoal(this, 0.15));
        this.goalSelector.addGoal(1, new BabyFlopAroundGoal(this));
        //this.goalSelector.addGoal(2, new BabyRSFollowParentGoal(this, 1.0));
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn)
    {
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
        if(this.tickCount > 72000) {
            if(!this.level.isClientSide) {
                this.remove(RemovalReason.DISCARDED);
                RocketSquidEntity adult = new RocketSquidEntity(this.level);
                Vec3 pos = this.position();
                adult.moveTo(pos.x, pos.y, pos.z, (float) this.squidCap.getRotYaw(), (float) this.squidCap.getRotPitch());
                this.level.addFreshEntity(adult);
            }
        }
        else {
            //Do on client and server
            //Fraction of distance to target rotation to rotate by each server tick
            double rotateSpeed;
            if(this.wasTouchingWater) {
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9);
                rotateSpeed = 0.06;
            }
            else {
                Vec3 oldMotion = this.getDeltaMovement();
                double motionX = oldMotion.x;
                double motionY = oldMotion.y;
                double motionZ = oldMotion.z;
                if(this.hurtTime > 0) {
                    motionX = 0.0D;
                    motionZ = 0.0D;
                }
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    motionY += 0.05D * (double)(Objects.requireNonNull(this.getEffect(MobEffects.LEVITATION)).getAmplifier() + 1) - motionY;
                }
                else if (!this.isNoGravity()) {
                    motionY -= 0.08D;
                }
                motionX *= 0.9800000190734863D;
                motionY *= 0.9800000190734863D;
                motionZ *= 0.9800000190734863D;
                rotateSpeed = 0.15;
                this.setDeltaMovement(motionX, motionY, motionZ);
            }

            //Rotate towards target pitch
            double trp = this.squidCap.getTargetRotPitch();
            double rp = this.squidCap.getRotPitch();
            if(trp != rp) {
                //Squids rotate <= 180 degrees either way.
                //The squid can rotate out of the interval [-PI, PI].
                rp += (trp - rp) * rotateSpeed;
                this.squidCap.setRotPitch(rp);
                this.newPacketRequired = true;
            }

            //Rotate towards target yaw
            double trY = this.squidCap.getTargetRotYaw();
            double ry = this.squidCap.getRotYaw();
            if(trY != ry) {
                ry += (trY - ry) * rotateSpeed;
                this.squidCap.setRotYaw(ry);
                this.newPacketRequired = true;
            }

            if(this.level.isClientSide) {
                //Client side
                //Handles tentacle angles
                this.lastTentacleAngle = this.tentacleAngle;
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.wasTouchingWater ? (float) ((Math.PI / 6) + (Mth.sin((float) Math.toRadians(4 * (this.tickCount % 360))) * Math.PI / 6)) : 0;
            }
            else {
                //Server side
                if(this.isInWater()) {
                    this.moveToWherePointing();
                }
                if(this.newPacketRequired) {
                    Vec3 pos = this.position();
                    MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64, this.level.dimension())), new MessageBabyCapData(this.getUUID(), this.squidCap));
                    this.newPacketRequired = false;
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

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("id", RocketSquidsBase.BABY_SQUID_TYPE.toString());
    }

    //////////////////////
    //CAPABILITY METHODS//
    //////////////////////
    public double getPrevRotPitch() {
        return this.squidCap.getPrevRotPitch();
    }

    public double getPrevRotYaw() {
        return this.squidCap.getPrevRotYaw();
    }

    public double getRotPitch() {
        return this.squidCap.getRotPitch();
    }

    public double getRotYaw() {
        return this.squidCap.getRotYaw();
    }

    public void setTargetRotPitch(double targPitch) {
        if(targPitch != this.squidCap.getTargetRotPitch()) {
            this.squidCap.setTargetRotPitch(targPitch);
            this.newPacketRequired = true;
        }
    }

    public void setTargetRotYaw(double targYaw) {
        if (targYaw != this.squidCap.getTargetRotYaw()) {
            this.squidCap.setTargetRotYaw(targYaw);
            this.newPacketRequired = true;
        }
    }

    public double getTargRotPitch() {
        return this.squidCap.getTargetRotPitch();
    }

    public double getTargRotYaw() {
        return this.squidCap.getTargetRotYaw();
    }
}
