// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import net.minecraft.world.phys.Vec3;

public class RotationHelper {
    public static final float PI_F = (float) Math.PI;

    /**
     * To convert an angle from degrees to radians multiply it by this
     */
    public static final double DEG2RAD = Math.PI / 180.0;

    /**
     * If a component of a direction vector is beyond this threshold the squid is considered pointing in that direction
     */
    public static final double DIRECTION_POINT_THRESHOLD = Math.sqrt(1.0/3.0) - 0.1;

    public static Vec3 getSquidDirection(RocketSquidEntity e) {
        double rp = e.getPitch();
        double ry = e.getYaw();
        double yDir = Math.cos(rp);
        double hozDir = Math.sin(rp);
        double zDir = hozDir * Math.cos(ry);
        double xDir = hozDir * -Math.sin(ry);
        return new Vec3(xDir, yDir, zDir);
    }
}
