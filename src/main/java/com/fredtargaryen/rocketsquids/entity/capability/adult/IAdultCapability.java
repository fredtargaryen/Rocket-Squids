package com.fredtargaryen.rocketsquids.entity.capability.adult;

/**
 * The capability used by all official Rocket Squids.
 * This capability was designed for exclusive use
 * with this mod; correct operation is not guaranteed
 * in any other context!
 */
public interface IAdultCapability {
    boolean getShaking();
    void setShaking(boolean b);
    int getShakeTicks();
    void setShakeTicks(int ticks);

    boolean getBlasting();
    void setBlasting(boolean b);

    double getPrevRotPitch();
    double getPrevRotYaw();
    
    double getRotPitch();
    void setRotPitch(double d);
    double getRotYaw();
    void setRotYaw(double d);
    
    double getTargetRotPitch();
    void setTargetRotPitch(double d);
    double getTargetRotYaw();
    void setTargetRotYaw(double d);

    boolean getBlastToStatue();
    void setBlastToStatue(boolean blast);

    boolean getForcedBlast();
    void setForcedBlast(boolean b);

    void processNote(byte note);
    byte[] getLatestNotes();
    void setLatestNotes(byte[] notes);
    byte[] getTargetNotes();
    void setTargetNotes(byte[] notes);
}
