package com.fredtargaryen.rocketsquids.entity.projectile;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class ThrownTubeEntity extends ProjectileItemEntity {
    public ThrownTubeEntity(World w) {
        super(RocketSquidsBase.TUBE_TYPE, w);
    }
    public ThrownTubeEntity(LivingEntity elb, World w)
    {
        super(RocketSquidsBase.TUBE_TYPE, elb, w);
    }
    public ThrownTubeEntity(FMLPlayMessages.SpawnEntity spawn, World world) {
        this(world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            Vector3d pos = this.getPositionVec();
            this.world.createExplosion(this.func_234616_v_(), pos.x, pos.y, pos.z, 1.0F, Explosion.Mode.NONE);
            this.remove();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return RocketSquidsBase.TURBO_TUBE;
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
