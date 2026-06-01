// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CountdownGoal extends Goal {
    private final RocketSquidEntity squid;

    public CountdownGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.squid.getShaking() && !this.squid.getBlasting();
    }

    @Override
    public void tick() {
        int ticksLeft = this.squid.getCountdownTicks();
        if(ticksLeft == -1) {
            //No countdown in progress; start one
            this.squid.setCountdownTicks((byte) 20);
        }
        else if(ticksLeft == 0) {
            this.squid.setBlasting(true);
            this.squid.setCountdownTicks((byte) -1);
        }
        else {
            this.squid.setCountdownTicks((byte) (ticksLeft - 1));
        }
    }
}
