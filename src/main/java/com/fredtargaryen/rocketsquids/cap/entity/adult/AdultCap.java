package com.fredtargaryen.rocketsquids.cap.entity.adult;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class AdultCap {
    private static final byte[] note_ids = {12, 14, 16, 17, 19, 21, 23};
    private int brokenNotes;

    private boolean shaking;
    private int shakeTicks;
    private boolean blasting;
    private boolean blastToStatue;
    private boolean forcedBlast;
    private final byte[] latestNotes;
    private byte[] targetNotes;

    //ALL RADIANS
    private double prevRotPitch;
    private double prevRotYaw;
    private double rotPitch;
    private double rotYaw;
    private double targRotPitch;
    private double targRotYaw;

    public AdultCap() {
        this.brokenNotes = 0;

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


    public boolean getShaking() {
        return this.shaking;
    }


    public void setShaking(boolean b) {
        this.shaking = b;
    }


    public int getShakeTicks() {
        return this.shakeTicks;
    }


    public void setShakeTicks(int ticks) {
        this.shakeTicks = ticks;
    }


    public boolean getBlasting() {
        return this.blasting;
    }


    public void setBlasting(boolean b) {
        this.blasting = b;
    }


    public double getPrevRotPitch() { return this.prevRotPitch; }


    public double getPrevRotYaw() { return this.prevRotYaw; }


    public double getRotPitch() {
        return this.rotPitch;
    }


    public void setRotPitch(double d) {
        this.prevRotPitch = this.rotPitch;
        this.rotPitch = d;
    }


    public double getRotYaw()
    {
        return this.rotYaw;
    }


    public void setRotYaw(double d) {
        this.prevRotYaw = this.rotYaw;
        this.rotYaw = d;
    }


    public double getTargetRotPitch() {
        return this.targRotPitch;
    }


    public void setTargetRotPitch(double d) {
        this.targRotPitch = d;
    }


    public double getTargetRotYaw() {
        return this.targRotYaw;
    }


    public void setTargetRotYaw(double d) {
        this.targRotYaw = d;
    }


    public boolean getBlastToStatue() {
        return this.blastToStatue;
    }


    public void setBlastToStatue(boolean blast) {
        this.blastToStatue = blast;
    }


    public boolean getForcedBlast()
    {
        return this.forcedBlast;
    }


    public void setForcedBlast(boolean b)
    {
        this.forcedBlast = b;
    }


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


    public byte[] getLatestNotes() {
        return this.latestNotes;
    }


    public void setLatestNotes(byte[] notes) {
        try {
            this.latestNotes[0] = notes[0];
            this.latestNotes[1] = notes[1];
            this.latestNotes[2] = notes[2];
        } catch (IndexOutOfBoundsException e) {
            this.brokenNotes++;
            if (!(this.brokenNotes > 3)) {
                RocketSquidsBase.LOGGER.error("Encountered IndexOutOfBoundsException skipping");
            }
        }
    }


    public byte[] getTargetNotes() {
        return this.targetNotes;
    }


    public void setTargetNotes(byte[] notes) {
        this.targetNotes = notes;
    }


    public CompoundTag saveNBT(CompoundTag comp) {
        comp.putDouble("pitch", this.getRotPitch());
        comp.putDouble("yaw", this.getRotYaw());
        comp.putDouble("targetPitch", this.getTargetRotPitch());
        comp.putDouble("targetYaw", this.getTargetRotYaw());
        comp.putBoolean("shaking", this.getShaking());
        comp.putInt("shaketicks", this.getShakeTicks());
        comp.putBoolean("blasting", this.getBlasting());
        comp.putBoolean("forcedblast", this.getForcedBlast());
        comp.putByteArray("latestnotes", this.getLatestNotes());
        comp.putByteArray("targetnotes", this.getTargetNotes());
        comp.putBoolean("blasttostatue", this.getBlastToStatue());
        return comp;
    }

    public CompoundTag loadNBT(CompoundTag comp) {
        this.setRotPitch(comp.getDouble("pitch"));
        this.setRotYaw(comp.getDouble("yaw"));
        this.setTargetRotPitch(comp.getDouble("targetPitch"));
        this.setTargetRotYaw(comp.getDouble("targetYaw"));
        this.setShaking(comp.getBoolean("shaking"));
        this.setShakeTicks(comp.getInt("shaketicks"));
        this.setBlasting(comp.getBoolean("blasting"));
        this.setForcedBlast(comp.getBoolean("forcedblast"));
        this.setLatestNotes(comp.getByteArray("latestnotes"));
        this.setTargetNotes(comp.getByteArray("targetnotes"));
        this.setBlastToStatue(comp.getBoolean("blasttostatue"));
        return comp;
    }
}
