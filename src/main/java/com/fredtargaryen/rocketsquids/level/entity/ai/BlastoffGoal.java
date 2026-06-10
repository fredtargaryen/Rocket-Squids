// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.TrickParameters;
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
        if (ticksLeft == 0) {
            // Blast finished; reset squid state
            this.squid.setShaking(false);
            if (this.squid.forcedBlast) {
                this.squid.explode();
                return;
            }
        }
        if (ticksLeft == 3 && !this.squid.getSaddled()) {
            // Decide whether the squid feels like doing a trick
            RandomSource r = this.squid.getRandom();
            if (r.nextInt(4) == 0) {
                // Generate random trick parameters
                byte forwardAxis = 0;
                byte sideAxis = 0;
                if (r.nextInt(3) == 0) forwardAxis = r.nextBoolean() ? (byte) 1 : (byte) -1;
                if (r.nextInt(3) == 0) sideAxis = r.nextBoolean() ? (byte) 1 : (byte) -1;
                TrickParameters tp = new TrickParameters(forwardAxis, sideAxis);
                this.squid.doTrick(tp);
            }
        }
        if (this.squid.forcedBlast && this.squid.areBlocksInWay()) {
            this.squid.explode();
            return;
        }
        RotationHelper.pointSquidInDirectionMoving(this.squid);
    }
}
