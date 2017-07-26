package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityThrownTube extends EntityThrowable
{

    public EntityThrownTube(World w) {
        super(w);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public EntityThrownTube(World w, EntityLivingBase elb)
    {
        super(w, elb);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote)
        {
            this.worldObj.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 0.1F, true);
            this.setDead();
        }
    }
}
