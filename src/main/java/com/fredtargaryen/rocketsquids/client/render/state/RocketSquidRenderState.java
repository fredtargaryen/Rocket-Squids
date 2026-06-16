package com.fredtargaryen.rocketsquids.client.render.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class RocketSquidRenderState extends LivingEntityRenderState {
    public boolean isBaby;
    public float tentacleAngle;
    public float xBodyRot;
    public float yBodyRot;
    public float yBodyRot2;
    public boolean saddled;
    public boolean shaking;
    public byte countdownTicks;
    public byte blastTicksRemaining;
    public boolean isInWater;
}
