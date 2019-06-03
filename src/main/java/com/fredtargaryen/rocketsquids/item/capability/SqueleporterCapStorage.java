package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * The storage class for ISqueleporter for all official Rocket Squids Squeleporters.
 * This storage class was designed for exclusive use with this item; correct
 * operation is not guaranteed in any other context!
 */
public class SqueleporterCapStorage implements Capability.IStorage<ISqueleporter>
{
    @Override
    public INBTBase writeNBT(Capability<ISqueleporter> capability, ISqueleporter instance, EnumFacing side)
    {
        NBTTagCompound comp = new NBTTagCompound();
        EntityRocketSquid ers = instance.getSquid();
        if(ers != null) ers.writeUnlessRemoved(comp);
        return comp;
    }

    @Override
    public void readNBT(Capability<ISqueleporter> capability, ISqueleporter instance, EnumFacing side, INBTBase nbt) {
        NBTTagCompound comp = (NBTTagCompound) nbt;
        EntityRocketSquid ers = new EntityRocketSquid(null);
        ers.read(comp);
        instance.setSquid(ers);
    }
}
