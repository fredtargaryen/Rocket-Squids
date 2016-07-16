package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityThrownTube extends EntityThrowable
{
    public EntityThrownTube(World w) { super(w); }
    public EntityThrownTube(World w, EntityPlayer p)
    {
        super(w, p);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null)
        {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }
        if (!this.worldObj.isRemote)
        {
            this.explode();
            this.setDead();
        }
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public TubeExplosion explode()
    {
        TubeExplosion explosion = new TubeExplosion(this.worldObj, this, this.posX, this.posY, this.posZ);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.worldObj, explosion)) return explosion;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }
}
