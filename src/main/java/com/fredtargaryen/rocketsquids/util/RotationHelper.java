// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

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

    public static ArrayList<Direction> getBlockDirectionsSquidIsPointing(RocketSquidEntity e) {
        ArrayList<Direction> directions = new ArrayList<>();
        Vec3 direction = RotationHelper.getSquidDirection(e);
        double t = RotationHelper.DIRECTION_POINT_THRESHOLD;
        if (direction.y > t) {
            directions.add(Direction.UP);
        } else if (direction.y < -t) {
            directions.add(Direction.DOWN);
        }
        //South is positive z I think
        if (direction.z > t) {
            directions.add(Direction.SOUTH);
        } else if (direction.z < -t) {
            directions.add(Direction.NORTH);
        }
        //East is positive x I think
        if (direction.x > t) {
            directions.add(Direction.EAST);
        } else if (direction.x < -t) {
            directions.add(Direction.WEST);
        }
        return directions;
    }

    /**
     * Get the direction the squid is pointing in.
     * This considers the default squid position (head up, tentacles down) to be the up vector.
     *
     * @param e The rocket squid
     * @return A unit vector representing the direction the squid is pointing in
     */
    public static Vec3 getSquidDirection(RocketSquidEntity e) {
        double rp = e.getPitch();
        double ry = e.getYaw();
        double yDir = Math.cos(rp);
        double hozDir = Math.sin(rp);
        double zDir = hozDir * Math.cos(ry);
        double xDir = hozDir * -Math.sin(ry);
        return new Vec3(xDir, yDir, zDir);
    }

    /**
     * Turn the entity based on its motion vector
     */
    public static void pointSquidInDirectionMoving(RocketSquidEntity squid) {
        Vec3 motion = squid.getDeltaMovement();
        if (!(Math.abs(motion.y) < 0.0785 && motion.x == 0.0 && motion.z == 0.0)) {
            //The aim is to find the local z movement to decide if the squid should pitch backwards or forwards.
            //The global z movement is given by squid.motionZ.
            //In addForce, squid.motionZ is given by horizontalForce * cos(yaw).
            //By rearranging, horizontalForce = this.motionZ / cos(yaw).
            //This is the amount by which the squid is moving along its own z axis (forwards or backwards).
            double speed = motion.z / Math.cos(squid.getYaw());
            squid.setTargetPitch(Math.PI / 2 - Math.atan2(motion.y, speed));
        }
    }

    /**
     * Get the squid's movement vector, and recalculate based on the current angle of the squid
     */
    public static void moveSquidInDirectionPointing(RocketSquidEntity e) {
        Vec3 motion = e.getDeltaMovement();
        double force = motion.length();
        Vec3 direction = getSquidDirection(e);
        e.setDeltaMovement(direction.scale(force));
        e.setOnGround(false);
    }

    /**
     * Set the rotation so that the squid is pointing along the desired direction vector
     * Sets the target rotation so will be a gradual turn for the squid.
     *
     * @param squid     The rocket squid to rotate
     * @param direction Unit vector representing intended squid direction
     * @param deviation Random addition to the angles so it doesn't look too perfect, if desired
     */
    public static void pointSquidInDirection(RocketSquidEntity squid, Vec3 direction, double deviation) {
        double hozDir = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        RandomSource r = squid.getRandom();
        squid.setTargetYaw(Math.acos(direction.z / hozDir) + deviation * (r.nextBoolean() ? 1 : -1));
        squid.setTargetPitch(Math.acos(direction.y) + deviation * (r.nextBoolean() ? 1 : -1));
    }
}
