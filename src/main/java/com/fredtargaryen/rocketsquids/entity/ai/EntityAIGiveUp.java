package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIGiveUp extends EntityAIBase
{
    private EntityRocketSquid squid;

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
        if (this.squid.isCollidedHorizontally) {
            this.squid.setTargetRotationPitch(-Math.PI);
        } else {
            this.squid.setTargetRotationPitch(Math.PI / 2);
        }
    }
}
