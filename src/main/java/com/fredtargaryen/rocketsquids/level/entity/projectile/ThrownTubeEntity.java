// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.projectile;

import com.fredtargaryen.rocketsquids.RSEntityTypes;
import com.fredtargaryen.rocketsquids.RSItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrownTubeEntity extends ThrowableItemProjectile {
    public ThrownTubeEntity(EntityType<? extends ThrownTubeEntity> type, Level world) {
        super(type, world);
    }

    public ThrownTubeEntity(ServerLevel level, LivingEntity owner, ItemStack stack) {
        super(RSEntityTypes.TUBE_TYPE.get(), owner, level, stack);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide()) {
            Vec3 pos = this.position();
            this.level().explode(this.getOwner(), pos.x, pos.y, pos.z, 1.0F, Level.ExplosionInteraction.NONE);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return RSItems.TURBO_TUBE.get();
    }

//    /**
//     * THIS IS REQUIRED FOR ALL NON-LIVING MOD ENTITIES FROM NOW ON
//     * Without this, they will not spawn on the client.
//     */
//    @Override
//    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
//        return NetworkHooks.getEntitySpawningPacket(this);
//    }
}
