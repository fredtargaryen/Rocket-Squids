package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class EntityAIRandomMove extends EntityAIBase
{
    private EntityRocketSquid squid;

    public EntityAIRandomMove(EntityRocketSquid squid)
    {
        this.squid = squid;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.squid.getPhase() == EntityRocketSquid.Phase.SWIM && this.squid.isInWater();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        int i = this.squid.getAge();

        if (i > 400)
        {
            this.squid.setRandomMotionVectors(0.0F, 0.0F, 0.0F);
        }
        else if (!this.squid.isMovingRandomly())
        {
            float f = this.squid.getRNG().nextFloat() * (float)Math.PI * 2.0F;
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.squid.setRandomMotionVectors(f1, f2, f3);
        }
    }
}
