// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.entity.TrickParameters;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TrickGoal extends Goal {
    private final RocketSquidEntity squid;
    private TrickParameters trickParams;

    private static final double boostSpeed = 1.8;

    // Such that a vector (dBS, dBS) has a magnitude of bS
    private static final double diagonalBoostSpeed = boostSpeed / Math.sqrt(2.0);

    // Used to rotate the squid a full revolution in 12 ticks
    private static final double angleIncrement = 2 * Math.PI / 12;

    public TrickGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.squid.getTricking();
    }

    @Override
    public void start() {
        this.trickParams = this.squid.trickParams;
        this.squid.trickParams = null;
        // Calculate the direction in which to do a little boost
        double boostZ = -1.0;
        double boostY = trickParams.forwardAxis() * (trickParams.sideAxis() == 0 ? boostSpeed : diagonalBoostSpeed);
        double boostX = trickParams.sideAxis() * (trickParams.forwardAxis() == 0 ? boostSpeed : diagonalBoostSpeed);
        this.squid.addDeltaMovement(RotationHelper.applySquidRotationFull(this.squid, new Vec3(boostX, boostY, boostZ)));
        this.squid.playSound(RSSounds.TRICK.get());
        Vec3 smokePos = this.squid.position().add(RotationHelper.applySquidRotationFull(this.squid, new Vec3(0.0, 0.0, 0.75)));
        ((ServerLevel) this.squid.level()).sendParticles(ParticleTypes.EXPLOSION.getType(), smokePos.x, smokePos.y, smokePos.z, 6, 0.5, 0.5, 0.5, 0.1D);
    }

    @Override
    public void tick() {
        byte ticksLeft = this.squid.getTrickTicksRemaining();
        if (this.trickParams.sideAxis() == 0) {
            this.squid.setTargetPitch(this.squid.getTargetPitch() + this.trickParams.forwardAxis() * angleIncrement);
        } else {
            // Got to be yaw first
            double yawChangeAmount = this.trickParams.forwardAxis() * this.trickParams.sideAxis() * (ticksLeft > 6 ? 0.5 : -0.5);
            this.squid.setTargetYaw(this.squid.getTargetYaw() + yawChangeAmount * angleIncrement);
            // Then pitch
            double pitchChangeAmount = this.trickParams.forwardAxis() * (ticksLeft > 9 || ticksLeft < 4 ? 0.5 : -0.5);
            this.squid.setTargetPitch(this.squid.getTargetPitch() + pitchChangeAmount * angleIncrement);
            // Then roll
            this.squid.setTargetRoll(this.squid.getTargetRoll() - this.trickParams.sideAxis() * angleIncrement);
        }
        this.squid.setTrickTicksRemaining(--ticksLeft);
    }
}
