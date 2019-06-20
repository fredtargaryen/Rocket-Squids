package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;

import java.util.concurrent.Callable;

/**
 * The implementation of IAdultCapability for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultSqueleporterImplFactory implements Callable<ISqueleporter> {
    public ISqueleporter call()
    {
        return new DefaultSqueleporterImpl();
    }

    private class DefaultSqueleporterImpl implements ISqueleporter {
        private RocketSquidEntity squid;

        public DefaultSqueleporterImpl() {

        }

        @Override
        public RocketSquidEntity getSquid() {
            return this.squid;
        }

        @Override
        public void setSquid(RocketSquidEntity ers) {
            this.squid = ers;
        }
    }
}