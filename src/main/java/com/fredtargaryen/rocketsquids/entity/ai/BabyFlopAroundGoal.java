package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BabyFlopAroundGoal extends Goal {
    private final BabyRocketSquidEntity squid;

    public BabyFlopAroundGoal(BabyRocketSquidEntity ebrs) {
        super();
        this.squid = ebrs;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        return !this.squid.isInWater();
    }

    @Override
    public void tick() {
        if (this.squid.onGround) {
            this.squid.setTargetRotPitch(Math.PI / 2);
        } else if(Math.abs(this.squid.getMotion().y) > 0.008){
            this.squid.pointToWhereMoving();
        }
    }
}
