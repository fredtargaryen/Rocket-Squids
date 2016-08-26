package com.fredtargaryen.rocketsquids.entity.capability;

import java.util.concurrent.Callable;

/**
 * The implementation of ISquidCapability for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultSquidImplFactory implements Callable<ISquidCapability>
{
    public ISquidCapability call()
    {
        return new DefaultSquidImpl();
    }

    private class DefaultSquidImpl implements ISquidCapability
    {
        private boolean shaking;
        private boolean blasting;
        private double prevRotPitch;
        private double prevRotYaw;
        private double rotPitch;
        private double rotYaw;
        private double targRotPitch;
        private double targRotYaw;
        private boolean forcedBlast;

        public DefaultSquidImpl()
        {
            this.shaking = false;
            this.blasting = false;
            this.prevRotPitch = 0.0;
            this.prevRotYaw = 0.0;
            this.rotPitch = 0.0;
            this.rotYaw = 0.0;
            this.targRotPitch = 0.0;
            this.targRotYaw = 0.0;
            this.forcedBlast = false;
        }

        @Override
        public boolean getShaking() {
            return this.shaking;
        }

        @Override
        public void setShaking(boolean b)
        {
            this.shaking = b;
        }

        @Override
        public boolean getBlasting() {
            return this.blasting;
        }

        @Override
        public void setBlasting(boolean b) {
            this.blasting = b;
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
        public void setRotPitch(double d)
        {
            this.prevRotPitch = this.rotPitch;
            this.rotPitch = d;
        }

        @Override
        public double getRotYaw()
        {
            return this.rotYaw;
        }

        @Override
        public void setRotYaw(double d)
        {
            this.prevRotYaw = this.rotYaw;
            this.rotYaw = d;
        }

        @Override
        public double getTargetRotPitch() {
            return this.targRotPitch;
        }

        @Override
        public void setTargetRotPitch(double d)
        {
            //Set current rotation to be within [-PI, PI].
            //Any operations on current rotation are also applied to target rotation.
            //Target rotation can be outside the interval; it will be
            //current rotation and brought back in next time this method is called.
            while(this.rotPitch < -Math.PI)
            {
                this.rotPitch += Math.PI * 2;
                d += Math.PI * 2;
            }
            while(this.rotPitch > Math.PI)
            {
                this.rotPitch -= Math.PI * 2;
                d -= Math.PI * 2;
            }
            this.prevRotPitch = this.rotPitch;
            this.targRotPitch = d;
        }

        @Override
        public double getTargetRotYaw() {
            return this.targRotYaw;
        }

        @Override
        public void setTargetRotYaw(double d)
        {
            //Set current rotation to be within [-PI, PI].
            //Any operations on current rotation are also applied to target rotation.
            //Target rotation can be outside the interval; it will be
            //current rotation and brought back in next time this method is called.
            while(this.rotYaw < -Math.PI)
            {
                this.rotYaw += Math.PI * 2;
                d += Math.PI * 2;
            }
            while(this.rotYaw > Math.PI)
            {
                this.rotYaw -= Math.PI * 2;
                d -= Math.PI * 2;
            }
            this.prevRotYaw = this.rotYaw;
            this.targRotYaw = d;
        }

        @Override
        public boolean getForcedBlast()
        {
            return this.forcedBlast;
        }

        @Override
        public void setForcedBlast(boolean b)
        {
            this.forcedBlast = b;
        }
    }
}