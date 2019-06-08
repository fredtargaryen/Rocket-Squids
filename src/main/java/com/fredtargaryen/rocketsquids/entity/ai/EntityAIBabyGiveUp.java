package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityBabyRocketSquid;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBabyGiveUp extends EntityAIBase {
    private final EntityBabyRocketSquid squid;

    public EntityAIBabyGiveUp(EntityBabyRocketSquid ebrs) {
        super();
        this.squid = ebrs;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return !this.squid.isInWater();
    }

    @Override
    public void tick() {
        if (this.squid.onGround) {
            this.squid.setTargetRotPitch(Math.PI / 2);
        } else if(Math.abs(this.squid.motionY) > 0.008){
            this.squid.pointToWhereFlying();
        }
    }
}
