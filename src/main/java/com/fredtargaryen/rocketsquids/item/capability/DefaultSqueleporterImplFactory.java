package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The implementation of ISquidCapability for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultSqueleporterImplFactory implements Callable<ISqueleporter> {
    public ISqueleporter call()
    {
        return new DefaultSqueleporterImpl();
    }

    private class DefaultSqueleporterImpl implements ISqueleporter {
        private EntityRocketSquid squid;

        public DefaultSqueleporterImpl() {

        }

        @Override
        public EntityRocketSquid getSquid() {
            return this.squid;
        }

        @Override
        public void setSquid(EntityRocketSquid ers) {
            this.squid = ers;
        }
    }
}