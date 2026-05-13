package com.fredtargaryen.rocketsquids.client.render.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RocketSquidRenderState extends LivingEntityRenderState {
    public float tentacleAngle;
    public float xBodyRot;
    public float yBodyRot;
    public boolean saddled;
    public boolean shaking;
    public boolean blasting;
    public boolean isInWater;
}
