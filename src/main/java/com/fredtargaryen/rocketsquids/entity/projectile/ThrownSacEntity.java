package com.fredtargaryen.rocketsquids.entity.projectile;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThrownSacEntity extends ThrowableItemProjectile {
    public ThrownSacEntity(final EntityType<? extends ThrownSacEntity> type, final Level world) {
        super(type, world);
    }
    public ThrownSacEntity(LivingEntity elb, Level w)
    {
        super(RocketSquidsBase.SAC_TYPE.get(), elb, w);
    }
    @SuppressWarnings("unused")
    public ThrownSacEntity(PlayMessages.SpawnEntity spawn, Level world) {
        this(RocketSquidsBase.SAC_TYPE.get(), world);
    }

    private static final MobEffect blindnessPotion = MobEffects.BLINDNESS;

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level.isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ertr = (EntityHitResult) result;
                ertr.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
            }
            AABB aabb = this.getBoundingBox().expandTowards(2.0D, 2.0D, 2.0D);
            List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, aabb);

            if (!list1.isEmpty()) {
                for (LivingEntity entitylivingbase : list1) {
                    if (entitylivingbase.isAffectedByPotions()) {
                        entitylivingbase.addEffect(new MobEffectInstance(blindnessPotion, 60));
                    }
                }
            }
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return RocketSquidsBase.NITRO_SAC.get();
    }

    /**
     * THIS IS REQUIRED FOR ALL NON-LIVING MOD ENTITIES FROM NOW ON
     * Without this, they will not spawn on the client.
     */
    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
