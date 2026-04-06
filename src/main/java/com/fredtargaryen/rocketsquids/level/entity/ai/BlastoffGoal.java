// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BlastoffGoal extends Goal {
    /**
     * Roughly how long blasts last, in AI ticks (of which there are only 10 per second)
     */
    private static final int blastTickLength = 15;
    private final RocketSquidEntity squid;
    private int blastTimer;

    public BlastoffGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.blastTimer = -1;
    }

    @Override
    public boolean canUse() {
        return this.squid.getBlasting() || this.squid.getForcedBlast();
    }

    @Override
    public void tick() {
        if (this.blastTimer < 0) {
            // Begin blast
            this.blastTimer = blastTickLength;
            this.squid.playSound(RSSounds.BLASTOFF.get(), 1.0F, 1.0F);
            this.squid.addForce(2.952);
        } else if (this.blastTimer > 0) {
            // Continue blast
            this.blastTimer--;
            this.squid.pointToWhereMoving();
        } else {
            if (this.squid.getForcedBlast()) {
                // Explode, so resetting doesn't matter
                this.squid.explode();
            } else {
                // Reset
                this.squid.setShaking(false);
                this.squid.setBlasting(false);
                this.squid.hasImpulse = false;
                this.blastTimer = -1;
            }
        }
    }
}
