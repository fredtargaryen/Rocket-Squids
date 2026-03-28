// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content.entity.ai;

import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AdultFlopAroundGoal extends Goal {
    private final RocketSquidEntity squid;

    public AdultFlopAroundGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.squid.isInWater() && !this.squid.isInLava() && !this.squid.getBlasting();
    }

    @Override
    public void tick() {
        if (this.squid.onGround()) {
            this.squid.setTargetRotPitch(Math.PI / 2);
        } else if(Math.abs(this.squid.getDeltaMovement().y) > 0.008){
            this.squid.pointToWhereMoving();
        }
    }
}
