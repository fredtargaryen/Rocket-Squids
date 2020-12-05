package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIGiveUp extends EntityAIBase
{
    private final EntityRocketSquid squid;

    public EntityAIGiveUp(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        return !this.squid.isInWater() && !this.squid.isInLava() && !this.squid.getBlasting();
    }

    @Override
    public void updateTask() {
        if (this.squid.onGround) {
            this.squid.setTargetRotPitch(Math.PI / 2);
        } else if(Math.abs(this.squid.motionY) > 0.008){
            this.squid.pointToWhereFlying();
        }
    }
}