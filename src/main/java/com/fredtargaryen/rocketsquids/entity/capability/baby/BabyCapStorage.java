package com.fredtargaryen.rocketsquids.entity.capability.baby;

import com.fredtargaryen.rocketsquids.util.capability.IStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for IAdultCapability for all official Rocket Squids.
 * This storage class was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class BabyCapStorage implements IStorage<IBabyCapability> {
    @Override
    public Tag writeNBT(Capability<IBabyCapability> capability, IBabyCapability instance, Direction side) {
        CompoundTag comp = new CompoundTag();
        comp.putDouble("pitch", instance.getRotPitch());
        comp.putDouble("yaw", instance.getRotYaw());
        comp.putDouble("targetPitch", instance.getTargetRotPitch());
        comp.putDouble("targetYaw", instance.getTargetRotYaw());
        return comp;
    }

    @Override
    public void readNBT(Capability<IBabyCapability> capability, IBabyCapability instance, Direction side, Tag nbt) {
        CompoundTag comp = (CompoundTag) nbt;
        instance.setRotPitch(comp.getDouble("pitch"));
        instance.setRotYaw(comp.getDouble("yaw"));
        instance.setTargetRotPitch(comp.getDouble("targetPitch"));
        instance.setTargetRotYaw(comp.getDouble("targetYaw"));
    }
}
