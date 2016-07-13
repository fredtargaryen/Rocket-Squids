package com.fredtargaryen.rocketsquids.entity.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class SquidCapStorage implements Capability.IStorage<ISquidCapability>
{
    @Override
    public NBTBase writeNBT(Capability<ISquidCapability> capability, ISquidCapability instance, EnumFacing side)
    {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setDouble("pitch", instance.getRotPitch());
        comp.setDouble("yaw", instance.getRotYaw());
        comp.setDouble("targetPitch", instance.getTargetRotPitch());
        comp.setDouble("targetYaw", instance.getTargetRotYaw());
        comp.setBoolean("shaking", instance.getShaking());
        comp.setBoolean("blasting", instance.getBlasting());
        return comp;
    }

    @Override
    public void readNBT(Capability<ISquidCapability> capability, ISquidCapability instance, EnumFacing side, NBTBase nbt)
    {
        NBTTagCompound comp = (NBTTagCompound) nbt;
        instance.setRotPitch(comp.getDouble("pitch"));
        instance.setRotYaw(comp.getDouble("yaw"));
        instance.setTargetRotPitch(comp.getDouble("targetPitch"));
        instance.setTargetRotYaw(comp.getDouble("targetYaw"));
        instance.setShaking(comp.getBoolean("shaking"));
        instance.setBlasting(comp.getBoolean("blasting"));
    }
}
