package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ShakeGoal extends Goal {
    private final RocketSquidEntity squid;
    private int ticksLeft;

    public ShakeGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.ticksLeft = -1;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getShaking();
    }

    @Override
    public void tick() {
        if(this.ticksLeft == -1) {
            //No shake in progress; start one
            this.ticksLeft = 15 + this.squid.getRNG().nextInt(45);
        }
        else if(this.ticksLeft == 0) {
            this.squid.setBlasting(true);
            this.ticksLeft = -1;
        }
        else {
            --this.ticksLeft;
        }
    }
}
