package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.ai.BabyFlopAroundGoal;
import com.fredtargaryen.rocketsquids.entity.ai.BabySwimAroundGoal;
import com.fredtargaryen.rocketsquids.entity.capability.baby.IBabyCapability;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageBabyCapData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class BabyRocketSquidEntity extends AbstractSquidEntity {
    private IBabyCapability squidCap;

    public BabyRocketSquidEntity(World w) {
        super(RocketSquidsBase.BABY_SQUID_TYPE, w);
        this.getCapability(RocketSquidsBase.BABYCAP).ifPresent(cap -> BabyRocketSquidEntity.this.squidCap = cap);
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAttributes();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void setAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0D);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BabySwimAroundGoal(this, 0.15));
        this.goalSelector.addGoal(1, new BabyFlopAroundGoal(this));
    }



    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return false;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void livingTick() {
        super.livingTick();
        if(this.ticksExisted > 72000) {
            if(!this.world.isRemote) {
                this.remove();
                RocketSquidEntity adult = new RocketSquidEntity(this.world);
                Vector3d pos = this.getPositionVec();
                adult.setLocationAndAngles(pos.x, pos.y, pos.z, (float) this.squidCap.getRotYaw(), (float) this.squidCap.getRotPitch());
                this.world.addEntity(adult);
            }
        }
        else {
            //Do on client and server
            //Fraction of distance to target rotation to rotate by each server tick
            double rotateSpeed;
            if(this.inWater) {
                Vector3d motion = this.getMotion();
                this.setMotion(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9);
                rotateSpeed = 0.06;
            }
            else {
                Vector3d oldMotion = this.getMotion();
                double motionX = oldMotion.x;
                double motionY = oldMotion.y;
                double motionZ = oldMotion.z;
                if(this.recentlyHit > 0) {
                    motionX = 0.0D;
                    motionZ = 0.0D;
                }
                if (this.isPotionActive(Effects.LEVITATION)) {
                    motionY += 0.05D * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1) - motionY;
                }
                else if (!this.hasNoGravity()) {
                    motionY -= 0.08D;
                }
                motionX *= 0.9800000190734863D;
                motionY *= 0.9800000190734863D;
                motionZ *= 0.9800000190734863D;
                rotateSpeed = 0.15;
                this.setMotion(motionX, motionY, motionZ);
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

            if(this.world.isRemote) {
                //Client side
                //Handles tentacle angles
                this.lastTentacleAngle = this.tentacleAngle;
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.inWater ? (float) ((Math.PI / 6) + (MathHelper.sin((float) Math.toRadians(4 * (this.ticksExisted % 360))) * Math.PI / 6)) : 0;
            }
            else {
                //Server side
                if(this.isInWater()) {
                    this.moveToWherePointing();
                }
                if(this.newPacketRequired) {
                    Vector3d pos = this.getPositionVec();
                    MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64, this.world.getDimensionKey())), new MessageBabyCapData(this.getUniqueID(), this.squidCap));
                    this.newPacketRequired = false;
                }
            }
        }
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    @Override
    protected boolean canDropLoot() {return false;}

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
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

    public double getTargRotPitch() { return this.squidCap.getTargetRotPitch(); }

    public double getTargRotYaw() { return this.squidCap.getTargetRotYaw(); }
}
