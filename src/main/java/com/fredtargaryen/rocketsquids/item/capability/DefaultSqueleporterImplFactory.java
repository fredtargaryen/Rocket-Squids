package com.fredtargaryen.rocketsquids.item.capability;

import net.minecraft.nbt.CompoundNBT;

import java.util.concurrent.Callable;

/**
 * The implementation of ISqueleporter for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultSqueleporterImplFactory implements Callable<ISqueleporter> {
    public ISqueleporter call()
    {
        return new DefaultSqueleporterImpl();
    }

    private class DefaultSqueleporterImpl implements ISqueleporter {
        private CompoundNBT squidData;
        private CompoundNBT squidCapabilityData;

        public DefaultSqueleporterImpl() {

        }

        @Override
        public CompoundNBT getSquidData() {
            return this.squidData;
        }

        @Override
        public void setSquidData(CompoundNBT nbt) {
            this.squidData = nbt;
        }

        @Override
        public CompoundNBT getSquidCapabilityData() {
            return this.squidCapabilityData;
        }

        @Override
        public void setSquidCapabilityData(CompoundNBT nbt) {
            this.squidCapabilityData = nbt;
        }
    }
}