package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityThrownTube extends EntityThrowable
{
    private Vec3d impactPos;

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
            this.impactPos = new Vec3d(this.posX, this.posY, this.posZ);
            this.worldObj.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 0.1F, true);
            this.setDead();
        }
    }
}
