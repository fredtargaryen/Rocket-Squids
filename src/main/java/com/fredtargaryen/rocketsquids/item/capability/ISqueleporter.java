package com.fredtargaryen.rocketsquids.item.capability;

import net.minecraft.nbt.CompoundNBT;

/**
 * The capability used by all official Rocket Squids Squeleporters.
 * This capability was designed for exclusive use with this item;
 * correct operation is not guaranteed in any other context!
 */
public interface ISqueleporter {
    void setSquidData(CompoundNBT nbt);
    CompoundNBT getSquidData();
}
