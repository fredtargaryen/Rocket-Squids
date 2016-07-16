package com.fredtargaryen.rocketsquids.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EntityThrownSac extends EntityThrowable
{
    private static final Potion blindnessPotion = Potion.getPotionFromResourceLocation("blindness");

    public EntityThrownSac(World w) { super(w); }
    public EntityThrownSac(World w, EntityPlayer p)
    {
        super(w, p);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.entityHit != null)
        {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }
        if (!this.worldObj.isRemote)
        {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().expand(2.0D, 2.0D, 2.0D);
            List<EntityLivingBase> list1 = this.worldObj.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

            if (!list1.isEmpty())
            {
                for (EntityLivingBase entitylivingbase : list1)
                {
                    if (entitylivingbase.canBeHitWithPotion())
                    {
                        entitylivingbase.addPotionEffect(new PotionEffect(blindnessPotion, 60));
                    }
                }
            }
            this.setDead();
        }
    }
}
