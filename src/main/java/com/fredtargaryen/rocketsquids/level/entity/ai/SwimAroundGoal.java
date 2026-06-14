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

        //Move if scheduled
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
            Entity pass = this.squid.getFirstPassenger();
            assert pass != null;
            double pp = (pass.getXRot() + 90.0F) * RotationHelper.DEG2RAD;
            double py = pass.getYHeadRot() * RotationHelper.DEG2RAD;
            this.squid.setTargetPitch(pp);
            this.squid.setTargetYaw(py);
        } else {
            if (blocked) {
                //Just point the opposite way
                Vec3 direction = RotationHelper.getSquidDirection(this.squid);
                RotationHelper.pointSquidInDirection(this.squid, direction.scale(-1), Math.PI / 3.0);
            } else {
                this.squid.setTargetPitch(this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1));
                this.squid.setTargetYaw(this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1));
            }
        }
        this.squid.setTargetRoll(this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1));
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 10 + this.r.nextInt(20);
    }
}
