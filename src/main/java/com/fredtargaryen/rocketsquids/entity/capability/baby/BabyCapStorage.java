package com.fredtargaryen.rocketsquids.entity.capability.baby;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for IAdultCapability for all official Rocket Squids.
 * This storage class was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class BabyCapStorage implements Capability.IStorage<IBabyCapability> {
    @Override
    public INBT writeNBT(Capability<IBabyCapability> capability, IBabyCapability instance, Direction side) {
        CompoundNBT comp = new CompoundNBT();
        comp.putDouble("pitch", instance.getRotPitch());
        comp.putDouble("yaw", instance.getRotYaw());
        comp.putDouble("targetPitch", instance.getTargetRotPitch());
        comp.putDouble("targetYaw", instance.getTargetRotYaw());
        return comp;
    }

    @Override
    public void readNBT(Capability<IBabyCapability> capability, IBabyCapability instance, Direction side, INBT nbt) {
        CompoundNBT comp = (CompoundNBT) nbt;
        instance.setRotPitch(comp.getDouble("pitch"));
        instance.setRotYaw(comp.getDouble("yaw"));
        instance.setTargetRotPitch(comp.getDouble("targetPitch"));
        instance.setTargetRotYaw(comp.getDouble("targetYaw"));
    }
}
