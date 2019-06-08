package com.fredtargaryen.rocketsquids.entity.capability.baby;

/**
 * The capability used by all official baby Rocket Squids.
 * This capability was designed for exclusive use
 * with this mod; correct operation is not guaranteed
 * in any other context!
 */
public interface IBabyCapability {
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
}
