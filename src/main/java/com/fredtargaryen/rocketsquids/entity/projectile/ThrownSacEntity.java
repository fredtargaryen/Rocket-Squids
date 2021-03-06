package com.fredtargaryen.rocketsquids.entity.projectile;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class ThrownSacEntity extends ProjectileItemEntity {
    private static final Effect blindnessPotion = Effects.BLINDNESS;

    public ThrownSacEntity(EntityType<? extends ThrownSacEntity> type, World w) { super(type, w); }
    public ThrownSacEntity(LivingEntity elb, World w)
    {
        super(RocketSquidsBase.SAC_TYPE, elb, w);
    }
    public ThrownSacEntity(FMLPlayMessages.SpawnEntity spawn, World world) {
        this(RocketSquidsBase.SAC_TYPE, world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult ertr = (EntityRayTraceResult) result;
                ertr.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 0.0F);
            }
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(2.0D, 2.0D, 2.0D);
            List<LivingEntity> list1 = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);

            if (!list1.isEmpty()) {
                for (LivingEntity entitylivingbase : list1) {
                    if (entitylivingbase.canBeHitWithPotion()) {
                        entitylivingbase.addPotionEffect(new EffectInstance(blindnessPotion, 60));
                    }
                }
            }
            this.remove();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return RocketSquidsBase.NITRO_SAC;
    }

    /**
     * THIS IS REQUIRED FOR ALL NON-LIVING MOD ENTITIES FROM NOW ON
     * Without this, they will not spawn on the client.
     */
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
