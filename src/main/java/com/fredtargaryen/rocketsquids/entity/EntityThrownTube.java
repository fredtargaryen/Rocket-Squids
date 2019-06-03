package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityThrownTube extends EntityThrowable {
    public EntityThrownTube(World w) {
        super(RocketSquidsBase.TUBE_TYPE, w);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EntityThrownTube(EntityLivingBase elb, World w)
    {
        super(RocketSquidsBase.TUBE_TYPE, elb, w);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            this.world.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 0.1F, true);
            this.remove();
        }
    }
}
