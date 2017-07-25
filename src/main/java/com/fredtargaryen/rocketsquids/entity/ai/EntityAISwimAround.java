package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

public class EntityAISwimAround extends EntityAIBase
{
    private final EntityRocketSquid squid;

    //FOR TESTING
    //private boolean goHorizontal = false;
    //private double[] angles = new double[]{-Math.PI, -3*Math.PI / 4, -Math.PI / 2, -Math.PI / 4, 0, Math.PI / 4, Math.PI / 2, 3 * Math.PI / 4};
    //private int currentAngle;

    /**
     * True if turning; false if swimming forwards
     */
    private boolean turning;

    private final Random r;
	private final double swimForce;

    public EntityAISwimAround(EntityRocketSquid ers, double swimForce)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.turning = false;
        this.r = this.squid.getRNG();
        this.swimForce = swimForce;
        //this.currentAngle = 0;
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    /**
     * Do a turn.
     * @param hasVIPRider whether there is a rider who is a VIP
     * @return whether a turn will be executed. It won't if the squid is pointing roughly where the rider is facing.
     */
    public boolean doTurn(boolean hasVIPRider)
    {
        if(hasVIPRider)
        {
            Entity pass = this.squid.getControllingPassenger();
            float pp = (float) ((pass.rotationPitch + 90.0F) * Math.PI / 180.0F);
            float py = (float) (pass.getRotationYawHead() * Math.PI / 180.0F);
            float sp = (float) this.squid.getRotPitch();
            float sy = (float) this.squid.getRotYaw();
            if(Math.abs(pp - sp) >= 0.005 || Math.abs(py - sy) >= 0.005) {
                this.squid.setTargetRotPitch(pp);
                this.squid.setTargetRotYaw(py);
                return true;
            }
            return false;
        }
        else {
            //Random doubles between -PI and PI, added to current rotation
            this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
            this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
            return true;
        }
    }

    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Adult odds:
     * 1/12 - starts to shake (hands over to EntityAIShake)
     * 4/12 - repeats action
     * 7/12 - goes from turning to swimming forward or vice versa
     * Baby odds:
     * 5/12 - repeats action
     * 7/12 - goes from turning to swimming forward or vice versa
     */
    @Override
    public void updateTask() {
        //Code for testing squid swimming and visuals.
        //If all uncommented, will swim in an octagon without shaking.
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
        boolean hasVIPRider = this.squid.hasVIPRider();
        if (this.turning) {
            double trp = this.squid.getTargRotPitch();
            double Try = this.squid.getTargRotYaw();
            if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
                //The last turn is as good as finished
                int randomInt = this.r.nextInt(12);
                if (!this.squid.isBaby() && randomInt == 0) {
                    this.squid.setShaking(true);
                } else {
                    if (hasVIPRider) {
                        if (!this.doTurn(true)) {
                            //Squid is pointing roughly where the rider is facing
                            this.squid.addForce(this.swimForce);
                            this.turning = false;
                        }
                    } else {
                        if (randomInt < 5) {
                            this.doTurn(false);
                        } else {
                            this.squid.addForce(this.swimForce);
                            this.turning = false;
                        }
                    }
                }
            }
        } else {
            if (Math.abs(this.squid.motionX) < 0.005 && Math.abs(this.squid.motionY) < 0.005
                    && Math.abs(this.squid.motionZ) < 0.005) {
                this.squid.isAirBorne = false;
                //Last forward swim is as good as finished
                int randomInt = this.r.nextInt(12);
                if (!this.squid.isBaby() && randomInt == 0) {
                    this.squid.setShaking(true);
                } else {
                    if (hasVIPRider) {
                        if (this.doTurn(true)) {
                            this.turning = true;
                        } else {
                            //Squid is pointing roughly where the rider is facing
                            this.squid.addForce(this.swimForce);
                        }
                    } else {
                        if (randomInt < 5) {
                            this.squid.addForce(this.swimForce);
                        } else {
                            this.doTurn(hasVIPRider);
                            this.turning = true;
                        }
                    }
                }
            }
        }
    }
}
