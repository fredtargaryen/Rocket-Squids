package com.fredtargaryen.rocketsquids.entity.capability;

import java.util.concurrent.Callable;

public class DefaultSquidImplFactory implements Callable<ISquidCapability>
{
    public ISquidCapability call()
    {
        return new DefaultSquidImpl();
    }

    private class DefaultSquidImpl implements ISquidCapability {
        private double targRotPitch;
        private double targRotYaw;
        private boolean shaking;
        private boolean blasting;

        @Override
        public boolean getShaking() {
            return this.shaking;
        }

        @Override
        public void setShaking(boolean b) {
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
        public double getTargetRotationPitch() {
            return this.targRotPitch;
        }

        @Override
        public void setTargetRotationPitch(double d) {
            this.targRotPitch = d;
        }

        @Override
        public double getTargetRotationYaw() {
            return this.targRotYaw;
        }

        @Override
        public void setTargetRotationYaw(double d) {
            this.targRotYaw = d;
        }
    }
}