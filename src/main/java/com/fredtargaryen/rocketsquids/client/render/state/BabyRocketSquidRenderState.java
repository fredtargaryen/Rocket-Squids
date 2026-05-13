package com.fredtargaryen.rocketsquids.client.render.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BabyRocketSquidRenderState extends LivingEntityRenderState {
    public float tentacleAngle;
    public float xBodyRot;
    public float yBodyRot;
}
