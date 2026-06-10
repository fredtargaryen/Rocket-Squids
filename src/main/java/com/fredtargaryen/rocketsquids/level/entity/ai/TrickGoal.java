// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

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

    private static final double boostSpeed = 2.0;

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
        Vec3 pos = this.squid.position();
        Vec3 smokePos = RotationHelper.applySquidRotationFull(this.squid, new Vec3(0.0, 0.0, 0.25));
        ((ServerLevel) this.squid.level()).sendParticles(ParticleTypes.LARGE_SMOKE.getType(), pos.x, pos.y, pos.z, 10, smokePos.x, smokePos.y, smokePos.z, 0.0D);
    }

    @Override
    public void tick() {
        byte ticksLeft = this.squid.getTrickTicksRemaining();
        this.squid.setTrickTicksRemaining(--ticksLeft);
        if (this.trickParams.sideAxis() == 0) {
            this.squid.setTargetPitch(this.squid.getTargetPitch() + this.trickParams.forwardAxis() * angleIncrement);
        } else {
            this.squid.setTargetRoll(this.squid.getTargetRoll() - this.trickParams.sideAxis() * angleIncrement);
            int pitchChangeAmount = this.trickParams.forwardAxis() * (ticksLeft > 9 || ticksLeft < 3 ? 1 : -1);
            this.squid.setTargetPitch(this.squid.getTargetPitch() + 0.5 * pitchChangeAmount * angleIncrement);
            int yawChangeAmount = this.trickParams.sideAxis() * this.trickParams.forwardAxis() * (ticksLeft < 7 ? -1 : 1);
            this.squid.setTargetYaw(this.squid.getTargetYaw() + 1.0 * yawChangeAmount * angleIncrement);
        }
    }
}
