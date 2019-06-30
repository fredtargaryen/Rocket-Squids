package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ShakeGoal extends Goal {
    private final RocketSquidEntity squid;

    public ShakeGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getShaking();
    }

    @Override
    public void tick() {
        int ticksLeft = this.squid.getShakeTicks();
        if(ticksLeft == -1) {
            //No shake in progress; start one
            this.squid.setShakeTicks(40);
        }
        else if(ticksLeft == 0) {
            this.squid.setBlasting(true);
            this.squid.setShakeTicks(-1);
        }
        else {
            this.squid.setShakeTicks(ticksLeft - 1);
        }
    }
}
