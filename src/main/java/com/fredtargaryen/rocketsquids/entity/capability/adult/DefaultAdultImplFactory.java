package com.fredtargaryen.rocketsquids.entity.capability.adult;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The implementation of IAdultCapability for all official adult Rocket Squids.
 * This implementation was designed for exclusive use with this mod;
 * correct operation is not guaranteed in any other context!
 */
public class DefaultAdultImplFactory implements Callable<IAdultCapability> {
    private static final byte[] note_ids = {12, 14, 16, 17, 19, 21, 23};
    private static final double doublePi = Math.PI * 2;
    public IAdultCapability call()
    {
        return new DefaultAdultImpl();
    }

    private class DefaultAdultImpl implements IAdultCapability {
        private boolean shaking;
        private int shakeTicks;
        private boolean blasting;
        private boolean blastToStatue;
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

        public DefaultAdultImpl() {
            this.shaking = false;
            this.shakeTicks = -1;
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
            this.blastToStatue = false;
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
        public int getShakeTicks() {
            return this.shakeTicks;
        }

        @Override
        public void setShakeTicks(int ticks) {
            this.shakeTicks = ticks;
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
            this.targRotPitch = d;
        }

        @Override
        public double getTargetRotYaw() {
            return this.targRotYaw;
        }

        @Override
        public void setTargetRotYaw(double d) {
            this.targRotYaw = d;
        }

        @Override
        public boolean getBlastToStatue() {
            return this.blastToStatue;
        }

        @Override
        public void setBlastToStatue(boolean blast) {
            this.blastToStatue = blast;
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
            this.latestNotes[0] = this.latestNotes[1];
            this.latestNotes[1] = this.latestNotes[2];
            this.latestNotes[2] = note;
            if(this.latestNotes[0] == this.targetNotes[0]
            && this.latestNotes[1] == this.targetNotes[1]
            && this.latestNotes[2] == this.targetNotes[2]) {
                this.blastToStatue = true;
            }
        }

        @Override
        public byte[] getLatestNotes() {
            return this.latestNotes;
        }

        @Override
        public void setLatestNotes(byte[] notes) {
            this.latestNotes[0] = notes[0];
            this.latestNotes[1] = notes[1];
            this.latestNotes[2] = notes[2];
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