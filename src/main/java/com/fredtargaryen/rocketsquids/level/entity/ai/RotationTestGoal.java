package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Used only to test the visuals of squid rotation.
 * Test code but I've had to write it many times so it may as well go into the repo for convenience
 */
public class RotationTestGoal extends Goal {
    private final RocketSquidEntity squid;

    public RotationTestGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    @Override
    public void tick() {
        this.squid.setTargetYaw(0 * RotationHelper.DEG2RAD);
        this.squid.setTargetPitch(0 * RotationHelper.DEG2RAD);
        this.squid.setTargetRoll(0 * RotationHelper.DEG2RAD);
    }
}
