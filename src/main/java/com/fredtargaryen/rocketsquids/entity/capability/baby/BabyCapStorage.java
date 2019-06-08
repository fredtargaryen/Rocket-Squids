package com.fredtargaryen.rocketsquids.entity.capability.baby;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for IAdultCapability for all official Rocket Squids.
 * This storage class was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class BabyCapStorage implements Capability.IStorage<IBabyCapability> {
    @Override
    public INBTBase writeNBT(Capability<IBabyCapability> capability, IBabyCapability instance, EnumFacing side) {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setDouble("pitch", instance.getRotPitch());
        comp.setDouble("yaw", instance.getRotYaw());
        comp.setDouble("targetPitch", instance.getTargetRotPitch());
        comp.setDouble("targetYaw", instance.getTargetRotYaw());
        return comp;
    }

    @Override
    public void readNBT(Capability<IBabyCapability> capability, IBabyCapability instance, EnumFacing side, INBTBase nbt) {
        NBTTagCompound comp = (NBTTagCompound) nbt;
        instance.setRotPitch(comp.getDouble("pitch"));
        instance.setRotYaw(comp.getDouble("yaw"));
        instance.setTargetRotPitch(comp.getDouble("targetPitch"));
        instance.setTargetRotYaw(comp.getDouble("targetYaw"));
    }
}
