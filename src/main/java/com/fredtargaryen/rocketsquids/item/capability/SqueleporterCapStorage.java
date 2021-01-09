package com.fredtargaryen.rocketsquids.item.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for ISqueleporter for all official Rocket Squids Squeleporters.
 * This storage class was designed for exclusive use with this item; correct
 * operation is not guaranteed in any other context!
 */
public class SqueleporterCapStorage implements Capability.IStorage<ISqueleporter> {
    @Override
    public INBT writeNBT(Capability<ISqueleporter> capability, ISqueleporter instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT normalSquidData = instance.getSquidData();
        CompoundNBT capSquidData = instance.getSquidCapabilityData();
        nbt.put("normal", normalSquidData == null ? new CompoundNBT() : normalSquidData);
        nbt.put("capability", capSquidData == null ? new CompoundNBT() : instance.getSquidCapabilityData());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ISqueleporter> capability, ISqueleporter instance, Direction side, INBT nbt) {
        CompoundNBT comp = (CompoundNBT) nbt;
        instance.setSquidData(comp.getCompound("normal"));
        instance.setSquidCapabilityData(comp.getCompound("capability"));
    }
}
