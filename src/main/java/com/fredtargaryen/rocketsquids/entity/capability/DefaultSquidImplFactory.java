package com.fredtargaryen.rocketsquids.entity.capability;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The implementation of ISquidCapability for all official Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultSquidImplFactory implements Callable<ISquidCapability> {
    private static final byte[] note_ids = {12, 14, 16, 17, 19, 21, 23};
    private static final double doublePi = Math.PI * 2;
    public ISquidCapability call()
    {
        return new DefaultSquidImpl();
    }

    private class DefaultSquidImpl implements ISquidCapability {
        private boolean shaking;
        private boolean blasting;
        private boolean forcedBlast;
        private byte[] latestNotes;
        private byte[] targetNotes;

        //ALL RADIANS
        private double prevRotPitch;
        private double prevRotYaw;
        private double rotPitch;
        private double rotYaw;
        private double targRotPitch;
        private double targRotYaw;

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
            this.latestNotes = new byte[] {36, 36, 36};
            Random notePicker = new Random();
            this.targetNotes = new byte[] {
                    note_ids[notePicker.nextInt(7)],
                    note_ids[notePicker.nextInt(7)],
                    note_ids[notePicker.nextInt(7)]
            };
        }

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
                this.rotPitch += doublePi;

            }
            while(d < -Math.PI)
            {
                d += doublePi;
            }
            while(this.rotPitch > Math.PI)
            {
                this.rotPitch -= doublePi;
                d -= doublePi;
            }
            while(d > Math.PI)
            {
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
        public void setTargetRotYaw(double d)
        {
            //Set current rotation to be within [-PI, PI].
            //Any operations on current rotation are also applied to target rotation.
            //Target rotation can be outside the interval; it will be
            //current rotation and brought back in next time this method is called.
            while(this.rotYaw < -Math.PI)
            {
                this.rotYaw += doublePi;
            }
            while(d < -Math.PI)
            {
                d += doublePi;
            }
            while(this.rotYaw > Math.PI)
            {
                this.rotYaw -= doublePi;
            }
            while(d > Math.PI)
            {
                d -= doublePi;
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

        @Override
        public void processNote(byte note) {
            this.latestNotes[2] = this.latestNotes[1];
            this.latestNotes[1] = this.latestNotes[0];
            this.latestNotes[0] = note;
            if(this.latestNotes[0] == this.targetNotes[0]
            && this.latestNotes[1] == this.targetNotes[1]
            && this.latestNotes[2] == this.targetNotes[2]) {
                //TODO
                this.setForcedBlast(true);
            }
        }

        @Override
        public byte[] getLatestNotes() {
            return this.latestNotes;
        }

        @Override
        public void setLatestNotes(byte[] notes) {
            this.latestNotes = notes;
        }

        @Override
        public byte[] getTargetNotes() {
            return this.targetNotes;
        }

        @Override
        public void setTargetNotes(byte[] notes) {
            this.targetNotes = notes;
        }
    }
}