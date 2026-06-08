// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlastoffGoal extends Goal {
    private final RocketSquidEntity squid;

    public BlastoffGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.squid.getBlastTicksRemaining() > 0;
    }

    @Override
    public void start() {
        RandomSource r = this.squid.getRandom();
        this.squid.setTargetRoll(this.squid.getSaddled() ? 0.0 : r.nextDouble() * Math.PI * (r.nextBoolean() ? 1 : -1));
    }

    @Override
    public void tick() {
        byte ticksLeft = this.squid.getBlastTicksRemaining();
        this.squid.setBlastTicksRemaining(--ticksLeft);
        if (this.squid.forcedBlast) {
            if (ticksLeft == 0 || this.squid.areBlocksInWay()){
                this.squid.explode();
                return;
            }
        }
        RotationHelper.pointSquidInDirectionMoving(this.squid);
    }

    @Override
    public void stop() {
        this.squid.setShaking(false);
        this.squid.setBlastTicksRemaining((byte) 0);
        this.squid.forcedBlast = false;
        this.squid.needsSync = false;
    }
}
