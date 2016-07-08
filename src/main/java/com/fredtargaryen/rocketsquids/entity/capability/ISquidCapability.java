package com.fredtargaryen.rocketsquids.entity.capability;

public interface ISquidCapability
{
    boolean getShaking();
    void setShaking(boolean b);
    boolean getBlasting();
    void setBlasting(boolean b);

    double getTargetRotationPitch();
    void setTargetRotationPitch(double d);
    double getTargetRotationYaw();
    void setTargetRotationYaw(double d);
}
