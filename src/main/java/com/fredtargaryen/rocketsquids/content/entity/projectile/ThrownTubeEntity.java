package com.fredtargaryen.rocketsquids.content.entity.projectile;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class ThrownTubeEntity extends ThrowableItemProjectile {
    public ThrownTubeEntity(EntityType<? extends ThrownTubeEntity> type, Level world) {
        super(type, world);
    }
    public ThrownTubeEntity(LivingEntity elb, Level world)
    {
        super(RocketSquidsBase.TUBE_TYPE.get(), elb, world);
    }
    public ThrownTubeEntity(PlayMessages.SpawnEntity spawn, Level world) {
        this(RocketSquidsBase.TUBE_TYPE.get(), world);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level.isClientSide) {
            Vec3 pos = this.position();
            this.level.explode(this.getOwner(), pos.x, pos.y, pos.z, 1.0F, Level.ExplosionInteraction.NONE);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return RocketSquidsBase.TURBO_TUBE.get();
    }

    /**
     * THIS IS REQUIRED FOR ALL NON-LIVING MOD ENTITIES FROM NOW ON
     * Without this, they will not spawn on the client.
     */
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
