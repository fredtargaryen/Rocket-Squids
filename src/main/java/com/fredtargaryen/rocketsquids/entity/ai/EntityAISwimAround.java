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
	
	/**
	 * For the test code in updateTask
	 */
//	private boolean goHorizontal;
//    private int currentAngle;
//    private final double[] angles = new double[]{  Math.PI,     Math.PI * 3 / 4,       Math.PI / 2,    Math.PI / 4,
//                                                    0.0, -Math.PI / 4,  -Math.PI / 2,   -Math.PI * 3 / 4 };

    public EntityAISwimAround(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.turning = true;
        this.r = this.squid.getRNG();
        //this.currentAngle = 0;
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
     * 1/15 - starts to shake (hands over to EntityAIShake)
     * 6/15 - repeats action
     * 8/15 - goes from turning to swimming forward or vice versa
     */
    public void updateTask()
    {
		//Code for testing squid swimming and visuals.
		//If all uncommented, will swim in an octagon without shaking.
//        this.goHorizontal = false;
//		if(this.turning)
//		{
//            double rp = this.squid.getRotPitch();
//            double trp = this.squid.getTargRotPitch();
//            double ry = this.squid.getRotYaw();
//            double Try = this.squid.getTargRotYaw();
//            if (Math.abs(Try - ry) < 0.005 && Math.abs(trp - rp) < 0.005)
//            {
//                this.squid.addForce(0.25);
//                this.turning = false;
//            }
//        }
//        else
//        {
//            if(Math.abs(this.squid.motionX) < 0.005 && Math.abs(this.squid.motionY) < 0.005
//                    && Math.abs(this.squid.motionZ) < 0.005)
//            {
//                if (this.goHorizontal)
//                {
//                    this.squid.setTargetRotPitch(Math.PI / 2);
//                    if (this.currentAngle == 7)
//                    {
//                        this.currentAngle = 0;
//                    }
//                    else
//                    {
//                        ++this.currentAngle;
//                    }
//                    this.squid.setTargetRotYaw(this.angles[this.currentAngle]);
//                }
//                else
//                {
//                    this.squid.setTargetRotYaw(0);
//                    if (this.currentAngle == 7)
//                    {
//                        this.currentAngle = 0;
//                    }
//                    else
//                    {
//                        ++this.currentAngle;
//                    }
//                    this.squid.setTargetRotPitch(this.angles[this.currentAngle]);
//                }
//                this.turning = true;
//            }
//        }


        double rp = this.squid.getRotPitch();
        double ry = this.squid.getRotYaw();
        if(turning)
        {
            double trp = this.squid.getTargRotPitch();
            double Try = this.squid.getTargRotYaw();
            if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005)
            {
                //The last turn is as good as finished
                int randomInt = this.r.nextInt(15);
                if (randomInt == 0)
                {
                    this.squid.setShaking(true);
                }
                else if (randomInt < 7)
                {
                    //Random doubles between -PI and PI, added to current rotation
                    this.squid.setTargetRotPitch(rp + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
                    this.squid.setTargetRotYaw(ry + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
                }
                else
                {
                    this.squid.addForce(0.35);
                    this.turning = false;
                }
            }
        }
        else
        {
            if(Math.abs(this.squid.motionX) < 0.005 && Math.abs(this.squid.motionY) < 0.005
                    && Math.abs(this.squid.motionZ) < 0.005)
            {
                //Last forward swim is as good as finished
                int randomInt = this.r.nextInt(15);
                if(randomInt == 0)
                {
                    this.squid.setShaking(true);
                }
                else if(randomInt < 7)
                {
                    this.squid.addForce(0.35);
                }
                else
                {
                    this.squid.setTargetRotPitch(rp + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
                    this.squid.setTargetRotYaw(ry + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
                    this.turning = true;
                }
            }
        }
    }
}
