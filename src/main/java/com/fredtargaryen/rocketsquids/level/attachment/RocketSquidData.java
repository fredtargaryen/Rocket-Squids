// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.attachment;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.Random;

public class RocketSquidData implements ValueIOSerializable {
    private static final double doublePi = Math.PI * 2;
    private static final byte[] note_ids = {12, 14, 16, 17, 19, 21, 23};

    private double prevRotPitch;
    private double prevRotYaw;
    private double rotPitch;
    private double rotYaw;
    private double targetRotPitch;
    private double targetRotYaw;

    private boolean shaking;
    private int shakeTicks;
    private boolean blasting;
    private boolean blastToStatue;
    private boolean forcedBlast;
    private int[] latestNotes;
    private int[] targetNotes;
    private int brokenNotes;

    public RocketSquidData() {
        this.prevRotPitch = 0.0;
        this.prevRotYaw = 0.0;
        this.rotPitch = 0.0;
        this.rotYaw = 0.0;
        this.targetRotPitch = 0.0;
        this.targetRotYaw = 0.0;

        this.shaking = false;
        this.shakeTicks = -1;
        this.blasting = false;
        this.blastToStatue = false;
        this.forcedBlast = false;
        this.latestNotes = new int[]{-1, -1, -1};
        Random notePicker = new Random();
        this.targetNotes = new int[]{
                note_ids[notePicker.nextInt(7)],
                note_ids[notePicker.nextInt(7)],
                note_ids[notePicker.nextInt(7)]
        };
        this.brokenNotes = 0;
    }

    public double getPrevRotPitch() {
        return this.prevRotPitch;
    }

    public void setPrevRotPitch(double p) {
        this.prevRotPitch = p;
    }

    public double getPrevRotYaw() {
        return this.prevRotYaw;
    }

    public void setPrevRotYaw(double y) {
        this.prevRotYaw = y;
    }

    public double getRotPitch() {
        return this.rotPitch;
    }

    public void setRotPitch(double p) {
        this.prevRotPitch = this.rotPitch;
        this.rotPitch = p;
    }

    public double getRotYaw() {
        return this.rotYaw;
    }

    public void setRotYaw(double y) {
        this.prevRotYaw = this.rotYaw;
        this.rotYaw = y;
    }

    public double getTargetRotPitch() {
        return this.targetRotPitch;
    }

    public void setTargetRotPitch(double p) {
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while (this.rotPitch < -Math.PI) {
            this.rotPitch += doublePi;

        }
        while (p < -Math.PI) {
            p += doublePi;
        }
        while (this.rotPitch > Math.PI) {
            this.rotPitch -= doublePi;
            p -= doublePi;
        }
        while (p > Math.PI) {
            p -= doublePi;
        }
        this.prevRotPitch = this.rotPitch;
        this.targetRotPitch = p;
    }

    public double getTargetRotYaw() {
        return this.targetRotYaw;
    }

    public void setTargetRotYaw(double y) {
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while (this.rotYaw < -Math.PI) {
            this.rotYaw += doublePi;
        }
        while (y < -Math.PI) {
            y += doublePi;
        }
        while (this.rotYaw > Math.PI) {
            this.rotYaw -= doublePi;
        }
        while (y > Math.PI) {
            y -= doublePi;
        }
        this.prevRotYaw = this.rotYaw;
        this.targetRotYaw = y;
    }

    public boolean getShaking() {
        return this.shaking;
    }

    public void setShaking(boolean s) {
        this.shaking = s;
    }

    public int getShakeTicks() {
        return this.shakeTicks;
    }

    public void setShakeTicks(int i) {
        this.shakeTicks = i;
    }

    public boolean getBlasting() {
        return this.blasting;
    }

    public void setBlasting(boolean b) {
        this.blasting = b;
    }

    public boolean getBlastToStatue() {
        return this.blastToStatue;
    }

    public void setBlastToStatue(boolean b) {
        this.blastToStatue = b;
    }

    public boolean getForcedBlast() {
        return this.forcedBlast;
    }

    public void setForcedBlast(boolean b) {
        this.forcedBlast = b;
    }

    public int[] getLatestNotes() {
        return this.latestNotes;
    }

    public void setLatestNotes(int[] ln) {
        try {
            this.latestNotes[0] = ln[0];
            this.latestNotes[1] = ln[1];
            this.latestNotes[2] = ln[2];
        } catch (IndexOutOfBoundsException e) {
            this.brokenNotes++;
            if (!(this.brokenNotes > 3)) {
                RocketSquidsBase.LOGGER.error("Encountered IndexOutOfBoundsException skipping");
            }
        }
    }

    public int[] getTargetNotes() {
        return this.targetNotes;
    }

    public void setTargetNotes(int[] tn) {
        this.targetNotes = tn;
    }

    public void processNote(int note) {
        this.latestNotes[0] = this.latestNotes[1];
        this.latestNotes[1] = this.latestNotes[2];
        this.latestNotes[2] = note;
        if (this.latestNotes[0] == this.targetNotes[0]
                && this.latestNotes[1] == this.targetNotes[1]
                && this.latestNotes[2] == this.targetNotes[2]) {
            this.blastToStatue = true;
        }
    }

    @Override
    public void serialize(ValueOutput vo) {
        vo.putDouble("pitch", this.rotPitch);
        vo.putDouble("yaw", this.rotYaw);
        vo.putDouble("targetPitch", this.targetRotPitch);
        vo.putDouble("targetYaw", this.targetRotYaw);
        vo.putBoolean("shaking", this.shaking);
        vo.putBoolean("blasting", this.blasting);
        vo.putBoolean("forcedblast", this.forcedBlast);
        vo.putIntArray("latestnotes", this.latestNotes);
        vo.putIntArray("targetnotes", this.targetNotes);
        vo.putBoolean("blasttostatue", this.blastToStatue);
    }

    @Override
    public void deserialize(ValueInput vi) {
        this.setRotPitch(vi.getDoubleOr("pitch", 0.0));
        this.setRotYaw(vi.getDoubleOr("yaw", 0.0));
        this.setTargetRotPitch(vi.getDoubleOr("targetPitch", 0.0));
        this.setTargetRotYaw(vi.getDoubleOr("targetYaw", 0.0));
        this.setShaking(vi.getBooleanOr("shaking", false));
        this.setBlasting(vi.getBooleanOr("blasting", false));
        this.setForcedBlast(vi.getBooleanOr("forcedblast", false));
        this.setLatestNotes(vi.getIntArray("latestnotes").orElse(new int[]{-1, -1, -1}));
        this.setTargetNotes(vi.getIntArray("targetnotes").orElse(new int[]{-1, -1, -1}));
        this.setBlastToStatue(vi.getBooleanOr("blasttostatue", false));
    }
}
