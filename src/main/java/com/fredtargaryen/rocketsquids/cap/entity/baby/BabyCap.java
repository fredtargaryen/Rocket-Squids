package com.fredtargaryen.rocketsquids.cap.entity.baby;

import com.fredtargaryen.rocketsquids.entity.capability.baby.IBabyCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

public class BabyCap {
    private static final double doublePi = Math.PI * 2;

    //ALL RADIANS
    private double prevRotPitch;
    private double prevRotYaw;
    private double rotPitch;
    private double rotYaw;
    private double targRotPitch;
    private double targRotYaw;

    public BabyCap() {
        this.prevRotPitch = 0.0;
        this.prevRotYaw = 0.0;
        this.rotPitch = 0.0;
        this.rotYaw = 0.0;
        this.targRotPitch = 0.0;
        this.targRotYaw = 0.0;
    }

    
    public double getPrevRotPitch() { return this.prevRotPitch; }

    
    public double getPrevRotYaw() { return this.prevRotYaw; }

    
    public double getRotPitch() {
        return this.rotPitch;
    }

    
    public void setRotPitch(double d) {
        this.prevRotPitch = this.rotPitch;
        this.rotPitch = d;
    }

    
    public double getRotYaw()
    {
        return this.rotYaw;
    }

    
    public void setRotYaw(double d) {
        this.prevRotYaw = this.rotYaw;
        this.rotYaw = d;
    }

    
    public double getTargetRotPitch() {
        return this.targRotPitch;
    }

    
    public void setTargetRotPitch(double d) {
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while(this.rotPitch < -Math.PI) {
            this.rotPitch += doublePi;

        }
        while(d < -Math.PI) {
            d += doublePi;
        }
        while(this.rotPitch > Math.PI) {
            this.rotPitch -= doublePi;
            d -= doublePi;
        }
        while(d > Math.PI) {
            d -= doublePi;
        }
        this.prevRotPitch = this.rotPitch;
        this.targRotPitch = d;
    }

    
    public double getTargetRotYaw() {
        return this.targRotYaw;
    }

    
    public void setTargetRotYaw(double d) {
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while(this.rotYaw < -Math.PI) {
            this.rotYaw += doublePi;
        }
        while(d < -Math.PI) {
            d += doublePi;
        }
        while(this.rotYaw > Math.PI) {
            this.rotYaw -= doublePi;
        }
        while(d > Math.PI) {
            d -= doublePi;
        }
        this.prevRotYaw = this.rotYaw;
        this.targRotYaw = d;
    }


    public CompoundTag saveNBT(CompoundTag comp) {
        comp.putDouble("pitch", this.getRotPitch());
        comp.putDouble("yaw", this.getRotYaw());
        comp.putDouble("targetPitch", this.getTargetRotPitch());
        comp.putDouble("targetYaw", this.getTargetRotYaw());
        return comp;
    }


    public CompoundTag loadNBT(CompoundTag comp) {
        this.setRotPitch(comp.getDouble("pitch"));
        this.setRotYaw(comp.getDouble("yaw"));
        this.setTargetRotPitch(comp.getDouble("targetPitch"));
        this.setTargetRotYaw(comp.getDouble("targetYaw"));
        return comp;
    }
}
