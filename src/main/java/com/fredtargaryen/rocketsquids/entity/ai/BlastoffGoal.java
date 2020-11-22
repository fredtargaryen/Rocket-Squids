package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.EnumSet;

public class BlastoffGoal extends Goal {
    private final RocketSquidEntity squid;
    private boolean blastStarted;
    private boolean horizontal;

    public BlastoffGoal(RocketSquidEntity ers)
    {
        super();
        this.squid = ers;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.blastStarted = false;
        this.horizontal = true;
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getBlasting() || this.squid.getForcedBlast();
    }

    @Override
    public void tick() {
        Vector3d motion = this.squid.getMotion();
        if (this.blastStarted) {
            //The squid is part of the way through a blast
            if((this.horizontal
                    && this.motionHasPeaked(motion.x)
                    && this.motionHasPeaked(motion.z))
                    || (!this.horizontal && this.motionHasPeaked(motion.y))) {
                //Squid has blasted but slowed down, i.e. end of blast
                this.squid.setShaking(false);
                this.squid.setBlasting(false);
                this.squid.isAirBorne = false;
                this.blastStarted = false;
                if(this.squid.getForcedBlast())
                {
                    this.squid.explode();
                }
            }
            else {
                this.squid.pointToWhereMoving();
            }
        }
        else {
            //Blast has not started yet
            this.squid.setShaking(false);
            this.squid.playSound(Sounds.BLASTOFF, 1.0F, 1.0F);
            this.squid.addForce(2.952);
            ArrayList<Direction> directionsPointing = this.squid.getDirectionsPointing();
            this.horizontal = !directionsPointing.contains(Direction.UP) && !directionsPointing.contains(Direction.DOWN);
            this.blastStarted = true;
        }
    }

    private boolean motionHasPeaked(double motion) {
        return Math.abs(motion) < 0.05;
    }
}
