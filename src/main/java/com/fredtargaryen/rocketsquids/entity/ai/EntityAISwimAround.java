package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISwimAround extends EntityAIBase
{
    private EntityRocketSquid squid;

    /**
     * True if turning; false if swimming forwards
     */
    private boolean turning;

    private int ticks;

    public EntityAISwimAround(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.turning = true;
        this.ticks = -1;
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
            double rp = this.squid.getRotPitch();
            double prp = this.squid.getPrevRotPitch();
            double ry = this.squid.getRotYaw();
            double pry = this.squid.getPrevRotYaw();
            if (Math.abs(rp - prp) < 0.005 && Math.abs(ry - pry) < 0.005) {
                if (this.ticks == -1) {
                    this.ticks = 60;
                } else if (this.ticks == 0) {
                    this.squid.setTargetRotPitch(Math.PI / 2);
                    this.squid.setTargetRotYaw(ry + Math.PI / 2);
                    this.turning = false;
                    this.ticks = -1;
                } else {
                    --this.ticks;
                }
//                int randomInt = this.squid.getRNG().nextInt(6);
//                if (randomInt == 0)
//                {
//                    this.squid.setShaking(true);
//                }
//                else if (randomInt > 2)
//                {
//                    Random r = this.squid.getRNG();
//                    //Random doubles between -PI and PI (or -180 and 180)
                    //Should be current rot +/- PI or less
//                    this.squid.setTargetRotPitch(r.nextDouble() * (Math.PI - 0.01) * (r.nextBoolean() ? 1 : -1));
//                    this.squid.setTargetRotYaw(r.nextDouble() * (Math.PI - 0.01) * (r.nextBoolean() ? 1 : -1));
//                }
//                else
//                {
//                    this.turning = false;
//                }
            }
        }
        else
        {
            if(Math.abs(this.squid.motionX) < 0.05 && Math.abs(this.squid.motionY) < 0.05
                    && Math.abs(this.squid.motionZ) < 0.05)
            {
                if(this.ticks == -1)
                {
                    this.ticks = 60;
                }
                else if(this.ticks == 0)
                {
                    this.squid.addForce(0.35);
                    this.turning = true;
                    this.ticks = -1;
                }
                else
                {
                    --this.ticks;
                }
//                int randomInt = this.squid.getRNG().nextInt(6);
//                if(randomInt == 0)
//                {
//                    this.squid.setShaking(true);
//                }
//                else if(randomInt > 2)
//                {
//                    this.squid.addForce(0.4);
//                }
//                else
//                {
//                    this.turning = true;
//                }
            }
        }
    }
}
