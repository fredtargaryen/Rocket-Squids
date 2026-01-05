package com.fredtargaryen.rocketsquids.entity.capability.adult;

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
public class AdultCapStorage implements IStorage<IAdultCapability> {
    @Override
    public Tag writeNBT(Capability<IAdultCapability> capability, IAdultCapability instance, Direction side) {
        CompoundTag comp = new CompoundTag();
        comp.putDouble("pitch", instance.getRotPitch());
        comp.putDouble("yaw", instance.getRotYaw());
        comp.putDouble("targetPitch", instance.getTargetRotPitch());
        comp.putDouble("targetYaw", instance.getTargetRotYaw());
        comp.putBoolean("shaking", instance.getShaking());
        comp.putInt("shaketicks", instance.getShakeTicks());
        comp.putBoolean("blasting", instance.getBlasting());
        comp.putBoolean("forcedblast", instance.getForcedBlast());
        comp.putByteArray("latestnotes", instance.getLatestNotes());
        comp.putByteArray("targetnotes", instance.getTargetNotes());
        comp.putBoolean("blasttostatue", instance.getBlastToStatue());
        return comp;
    }

    @Override
    public void readNBT(Capability<IAdultCapability> capability, IAdultCapability instance, Direction side, Tag nbt) {
        CompoundTag comp = (CompoundTag) nbt;
        instance.setRotPitch(comp.getDouble("pitch"));
        instance.setRotYaw(comp.getDouble("yaw"));
        instance.setTargetRotPitch(comp.getDouble("targetPitch"));
        instance.setTargetRotYaw(comp.getDouble("targetYaw"));
        instance.setShaking(comp.getBoolean("shaking"));
        instance.setShakeTicks(comp.getInt("shaketicks"));
        instance.setBlasting(comp.getBoolean("blasting"));
        instance.setForcedBlast(comp.getBoolean("forcedblast"));
        instance.setLatestNotes(comp.getByteArray("latestnotes"));
        instance.setTargetNotes(comp.getByteArray("targetnotes"));
        instance.setBlastToStatue(comp.getBoolean("blasttostatue"));
    }
}
