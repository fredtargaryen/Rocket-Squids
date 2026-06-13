// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SwimAroundGoal extends Goal {
    private final RocketSquidEntity squid;
    private int tickCounter;
    private int nextScheduledMove;
    private final RandomSource r;

    public SwimAroundGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.r = this.squid.getRandom();
        this.tickCounter = 0;
        this.nextScheduledMove = 0;
    }

    @Override
    public boolean canUse() {
        return this.squid.isInWater();
    }

    @Override
    public void tick() {
        ++this.tickCounter;
        if (this.tickCounter > this.nextScheduledMove) this.scheduleNextMove();

        //Move and play notes if scheduled
        if (this.tickCounter == this.nextScheduledMove) {
            if (this.squid.isBaby()) {
                int randomInt = this.r.nextInt(2);
                if (randomInt == 0) {
                    this.doTurn(false, this.squid.areBlocksInWay());
                    this.scheduleNextMove();
                } else {
                    if (!this.squid.areBlocksInWay()) this.squid.addForce(0.15);
                    this.scheduleNextMove();
                }
            } else {
                int randomInt = this.r.nextInt(11);
                if (randomInt == 0) {
                    if (!this.squid.areBlocksInWay()) {
                        this.squid.beginCountdown();
                    }
                } else if (randomInt < 6) {
                    this.doTurn(this.squid.getFirstPassenger() instanceof Player, this.squid.areBlocksInWay());
                    this.scheduleNextMove();
                } else {
                    if (!this.squid.areBlocksInWay()) this.squid.addForce(0.25);
                    this.scheduleNextMove();
                }
            }
        }
    }

    /**
     * Do a turn.
     *
     * @param playerIsRiding whether a player is riding the squid
     * @param blocked        in the directions the squid is mainly pointing in, whether there are blocks in the way
     */
    public void doTurn(boolean playerIsRiding, boolean blocked) {
        if (playerIsRiding) {
            //Rider rotations are clamped to [-PI, PI]; squid rotations are not.
            //Therefore need to work in terms of this range, or risk squids spinning ridiculous amounts if they have
            //turned around many times before being ridden.
            Entity pass = this.squid.getFirstPassenger();
            assert pass != null;
            float pp = (float) ((pass.getXRot() + 90.0F) * Math.PI / 180.0F);
            float py = (float) (pass.getYHeadRot() * Math.PI / 180.0F);
            float unclamped_sp = (float) this.squid.getPitch();
            float unclamped_sy = (float) this.squid.getYaw();
            //Clamp them to [-2PI, 2PI]
            float clamped_sp = unclamped_sp;
            while (clamped_sp > Math.PI * 2) clamped_sp -= (float) (Math.PI * 2);
            while (clamped_sp < -Math.PI * 2) clamped_sp += (float) (Math.PI * 2);
            float clamped_sy = unclamped_sy;
            while (clamped_sy > Math.PI * 2) clamped_sy -= (float) (Math.PI * 2);
            while (clamped_sy < -Math.PI * 2) clamped_sy += (float) (Math.PI * 2);
            float pitchDiff = pp - clamped_sp;
            float yawDiff = py - clamped_sy;
            if (Math.abs(pitchDiff) >= 0.005 || Math.abs(yawDiff) >= 0.005) {
                //Player rotation is sufficiently far from squid rotation for the squid to start a new turn
                //Turn by the difference in rotations, to avoid having to spin into the [-PI, PI] range
                this.squid.setTargetPitch(unclamped_sp + pitchDiff);
                this.squid.setTargetYaw(unclamped_sy + yawDiff);
            }
        } else {
            if (blocked) {
                //Just point the opposite way
                Vec3 direction = RotationHelper.getSquidDirection(this.squid);
                RotationHelper.pointSquidInDirection(this.squid, direction.scale(-1), Math.PI / 3.0);
            } else {
                //Random doubles between -PI and PI, added to current rotation
                this.squid.setTargetPitch(this.squid.getPitch() + (this.r.nextDouble() * Math.PI / 4.0 * (this.r.nextBoolean() ? 1 : -1)));
                this.squid.setTargetYaw(this.squid.getYaw() + (this.r.nextDouble() * Math.PI / 4.0 * (this.r.nextBoolean() ? 1 : -1)));
            }
        }
        this.squid.setTargetRoll(this.squid.getRoll() + (this.r.nextDouble() * Math.PI / 4.0 * (this.r.nextBoolean() ? 1 : -1)));
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 10 + this.r.nextInt(20);
    }
}
