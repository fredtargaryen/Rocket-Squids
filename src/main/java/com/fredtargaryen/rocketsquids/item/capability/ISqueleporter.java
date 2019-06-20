package com.fredtargaryen.rocketsquids.item.capability;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;

/**
 * The capability used by all official Rocket Squids Squeleporters.
 * This capability was designed for exclusive use with this item;
 * correct operation is not guaranteed in any other context!
 */
public interface ISqueleporter {
    void setSquid(RocketSquidEntity ers);
    RocketSquidEntity getSquid();
}
