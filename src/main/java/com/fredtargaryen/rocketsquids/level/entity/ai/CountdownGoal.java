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
        return this.squid.getCountdownTicks() > 0;
    }

    @Override
    public void tick() {
        byte ticksLeft = this.squid.getCountdownTicks();
        this.squid.setCountdownTicks(--ticksLeft);
        if(ticksLeft == 0) {
            this.squid.blastoff();
        }
    }
}
