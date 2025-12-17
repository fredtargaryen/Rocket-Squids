package com.fredtargaryen.rocketsquids.item.capability;

import net.minecraft.nbt.CompoundTag;

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

    private static class DefaultSqueleporterImpl implements ISqueleporter {
        private CompoundTag squidData;
        private CompoundTag squidCapabilityData;

        public DefaultSqueleporterImpl() {

        }

        @Override
        public CompoundTag getSquidData() {
            return this.squidData;
        }

        @Override
        public void setSquidData(CompoundTag nbt) {
            this.squidData = nbt;
        }

        @Override
        public CompoundTag getSquidCapabilityData() {
            return this.squidCapabilityData;
        }

        @Override
        public void setSquidCapabilityData(CompoundTag nbt) {
            this.squidCapabilityData = nbt;
        }
    }
}