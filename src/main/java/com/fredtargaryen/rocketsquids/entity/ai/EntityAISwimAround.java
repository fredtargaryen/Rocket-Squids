package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Random;

public class EntityAISwimAround extends EntityAIBase
{
    private EntityRocketSquid squid;

    /**
     * True if turning; false if swimming forwards
     */
    private boolean turning;

    public EntityAISwimAround(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.turning = true;
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.isInWater();
    }

    @Override
    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Odds:
     * 1/6 - starts to shake (hands over to EntityAIShake)
     * 2/6 - switches action
     * 3/6 - repeats action
     */
    public void updateTask()
    {
        if(turning)
        {
            if (Math.abs(this.squid.rotationPitch - this.squid.prevRotationPitch) < 0.005
                    && Math.abs(this.squid.rotationYaw - this.squid.prevRotationYaw) < 0.005)
            {
                int randomInt = this.squid.getRNG().nextInt(6);
                if (randomInt == 0)
                {
                    this.squid.setShaking(true);
                }
                else if (randomInt > 2)
                {
                    Random r = this.squid.getRNG();
                    //Random doubles between -PI and PI (or -180 and 180)
                    this.squid.setTargetRotationPitch(r.nextDouble() * Math.PI * (r.nextBoolean() ? 1 : -1));
                    this.squid.setTargetRotationYaw(r.nextDouble() * Math.PI * (r.nextBoolean() ? 1 : -1));
                }
                else
                {
                    this.turning = false;
                }
            }
        }
        else
        {
            if(Math.abs(this.squid.motionX) < 0.05 && Math.abs(this.squid.motionY) < 0.05
                    && Math.abs(this.squid.motionZ) < 0.05)
            {
                int randomInt = this.squid.getRNG().nextInt(6);
                if(randomInt == 0) {
                    this.squid.setShaking(true);
                }
                else if(randomInt > 2)
                {
                    this.squid.addForce(1.0);
                }
                else
                {
                    this.turning = true;
                }
            }
        }
    }
}
