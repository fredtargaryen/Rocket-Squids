package com.fredtargaryen.rocketsquids.entity.capability;

public interface ISquidCapability
{
    boolean getShaking();
    void setShaking(boolean b);
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
}
