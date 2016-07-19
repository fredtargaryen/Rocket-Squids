package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Random;

public class EntityAISwimAround extends EntityAIBase
{
    private EntityRocketSquid squid;

    /**
     * True if turning; false if swimming forwards
     */
    private boolean turning;

    private Random r;

    public EntityAISwimAround(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.turning = true;
        this.r = this.squid.getRNG();
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    @Override
    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Odds:
     * 1/20 - starts to shake (hands over to EntityAIShake)
     * 6/20 - repeats action
     * 13/20 - goes from turning to swimming forward or vice versa
     */
    public void updateTask()
    {
        if(turning)
        {
            double rp = this.squid.getRotPitch();
            double prp = this.squid.getPrevRotPitch();
            double ry = this.squid.getRotYaw();
            double pry = this.squid.getPrevRotYaw();
            if (Math.abs(rp - prp) < 0.005 && Math.abs(ry - pry) < 0.005)
            {
                //The last turn is as good as finished
                int randomInt = this.r.nextInt(20);
//                if (randomInt == 0)
//                {
//                    this.squid.setShaking(true);
//                }
//                else
if (randomInt < 7)
                {
                    //Random doubles between -PI and PI, added to current rotation
                    this.squid.setTargetRotPitch(rp + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
                    this.squid.setTargetRotYaw(ry + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
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
                //Last forward swim is as good as finished
                int randomInt = this.r.nextInt(20);
//                if(randomInt == 0)
//                {
//                    this.squid.setShaking(true);
//                }
//                else
if(randomInt < 7)
                {
                    this.squid.addForce(0.35);
                }
                else
                {
                    this.turning = true;
                }
            }
        }
    }
}
