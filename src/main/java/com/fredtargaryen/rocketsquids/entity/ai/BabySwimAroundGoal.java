package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Random;

public class BabySwimAroundGoal extends Goal {
    private final BabyRocketSquidEntity squid;

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

    public BabySwimAroundGoal(BabyRocketSquidEntity ebrs, double swimForce) {
        super();
        this.squid = ebrs;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.turning = false;
        this.r = this.squid.getRNG();
        this.swimForce = swimForce;
        //this.currentAngle = 0;
    }

    @Override
    public boolean shouldExecute() {
        return this.squid.isInWater();
    }

    /**
     * Do a turn.
     * @return whether a turn will be executed (always true).
     */
    public boolean doTurn() {
        //Random doubles between -PI and PI, added to current rotation
        this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
        this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
        return true;
    }

    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Odds:
     * 5/12 - repeats action
     * 7/12 - goes from turning to swimming forward or vice versa
     */
    @Override
    public void tick() {
        //Code for testing squid swimming and visuals.
        //If all uncommented, will swim in an octagon.
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
        if (this.turning) {
            double trp = this.squid.getTargRotPitch();
            double Try = this.squid.getTargRotYaw();
            if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
                //The last turn is as good as finished
                int randomInt = this.r.nextInt(12);
                if (randomInt < 5) {
                    this.doTurn();
                }
                else {
                    this.squid.addForce(this.swimForce);
                    this.turning = false;
                }
            }
        }
        else {
            Vec3d motion = this.squid.getMotion();
            if (Math.abs(motion.x) < 0.005 && Math.abs(motion.y) < 0.005
                    && Math.abs(motion.z) < 0.005) {
                //Last forward swim is as good as finished
                int randomInt = this.r.nextInt(12);
                if (randomInt < 5) {
                    this.squid.addForce(this.swimForce);
                }
                else {
                    this.doTurn();
                    this.turning = true;
                }
            }
        }
    }
}
