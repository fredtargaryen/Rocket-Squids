package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.util.capability.IStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for ISqueleporter for all official Rocket Squids Squeleporters.
 * This storage class was designed for exclusive use with this item; correct
 * operation is not guaranteed in any other context!
 */
public class SqueleporterCapStorage implements IStorage<ISqueleporter> {
    @Override
    public Tag writeNBT(Capability<ISqueleporter> capability, ISqueleporter instance, Direction side) {
        CompoundTag nbt = new CompoundTag();
        CompoundTag normalSquidData = instance.getSquidData();
        CompoundTag capSquidData = instance.getSquidCapabilityData();
        nbt.put("normal", normalSquidData == null ? new CompoundTag() : normalSquidData);
        nbt.put("capability", capSquidData == null ? new CompoundTag() : instance.getSquidCapabilityData());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ISqueleporter> capability, ISqueleporter instance, Direction side, Tag nbt) {
        CompoundTag comp = (CompoundTag) nbt;
        instance.setSquidData(comp.getCompound("normal"));
        instance.setSquidCapabilityData(comp.getCompound("capability"));
    }
}
