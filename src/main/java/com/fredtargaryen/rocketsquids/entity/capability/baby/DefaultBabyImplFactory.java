package com.fredtargaryen.rocketsquids.entity.capability.baby;

import java.util.concurrent.Callable;

/**
 * The implementation of IAdultCapability for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultBabyImplFactory implements Callable<IBabyCapability> {
    private static final double doublePi = Math.PI * 2;
    public IBabyCapability call()
    {
        return new DefaultBabyImpl();
    }

    private class DefaultBabyImpl implements IBabyCapability {
        //ALL RADIANS
        private double prevRotPitch;
        private double prevRotYaw;
        private double rotPitch;
        private double rotYaw;
        private double targRotPitch;
        private double targRotYaw;

        public DefaultBabyImpl() {
            this.prevRotPitch = 0.0;
            this.prevRotYaw = 0.0;
            this.rotPitch = 0.0;
            this.rotYaw = 0.0;
            this.targRotPitch = 0.0;
            this.targRotYaw = 0.0;
        }

        @Override
        public double getPrevRotPitch() { return this.prevRotPitch; }

        @Override
        public double getPrevRotYaw() { return this.prevRotYaw; }

        @Override
        public double getRotPitch() {
            return this.rotPitch;
        }

        @Override
        public void setRotPitch(double d) {
            this.prevRotPitch = this.rotPitch;
            this.rotPitch = d;
        }

        @Override
        public double getRotYaw()
        {
            return this.rotYaw;
        }

        @Override
        public void setRotYaw(double d) {
            this.prevRotYaw = this.rotYaw;
            this.rotYaw = d;
        }

        @Override
        public double getTargetRotPitch() {
            return this.targRotPitch;
        }

        @Override
        public void setTargetRotPitch(double d) {
            //Set current rotation to be within [-PI, PI].
            //Any operations on current rotation are also applied to target rotation.
            //Target rotation can be outside the interval; it will be
            //current rotation and brought back in next time this method is called.
            while(this.rotPitch < -Math.PI) {
                this.rotPitch += doublePi;

            }
            while(d < -Math.PI) {
                d += doublePi;
            }
            while(this.rotPitch > Math.PI) {
                this.rotPitch -= doublePi;
                d -= doublePi;
            }
            while(d > Math.PI) {
                d -= doublePi;
            }
            this.prevRotPitch = this.rotPitch;
            this.targRotPitch = d;
        }

        @Override
        public double getTargetRotYaw() {
            return this.targRotYaw;
        }

        @Override
        public void setTargetRotYaw(double d) {
            //Set current rotation to be within [-PI, PI].
            //Any operations on current rotation are also applied to target rotation.
            //Target rotation can be outside the interval; it will be
            //current rotation and brought back in next time this method is called.
            while(this.rotYaw < -Math.PI) {
                this.rotYaw += doublePi;
            }
            while(d < -Math.PI) {
                d += doublePi;
            }
            while(this.rotYaw > Math.PI) {
                this.rotYaw -= doublePi;
            }
            while(d > Math.PI) {
                d -= doublePi;
            }
            this.prevRotYaw = this.rotYaw;
            this.targRotYaw = d;
        }
    }
}