package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
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
        return instance.getSquidData();
    }

    @Override
    public void readNBT(Capability<ISqueleporter> capability, ISqueleporter instance, Direction side, INBT nbt) {
        instance.setSquidData((CompoundNBT) nbt);
    }
}
