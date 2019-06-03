package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EntityThrownSac extends EntityThrowable {
    private static final Potion blindnessPotion = MobEffects.BLINDNESS;

    public EntityThrownSac(World w) { super(RocketSquidsBase.SAC_TYPE, w); }
    public EntityThrownSac(EntityLivingBase elb, World w)
    {
        super(RocketSquidsBase.SAC_TYPE, elb, w);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entity != null) {
            result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }
        if (!this.world.isRemote) {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().expand(2.0D, 2.0D, 2.0D);
            List<EntityLivingBase> list1 = this.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

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
            this.remove();
        }
    }
}
