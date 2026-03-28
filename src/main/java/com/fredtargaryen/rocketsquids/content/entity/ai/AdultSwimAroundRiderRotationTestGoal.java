package com.fredtargaryen.rocketsquids.content.entity.ai;

import com.fredtargaryen.rocketsquids.content.entity.RocketSquidEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Used only to test the visuals of the rotation of a squid rider.
 * Test code but I've had to write in many times so it may as well go into the repo for convenience
 */
public class AdultSwimAroundRiderRotationTestGoal extends Goal {
    private final RocketSquidEntity squid;
    private int tickCounter;
    private int nextScheduledMove;

    private final RandomSource r;
    private final double swimForce;

    // True for turn, false for move
    private boolean moveAction;

    public AdultSwimAroundRiderRotationTestGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.r = this.squid.getRandom();
        this.swimForce = 0.25;
        this.tickCounter = 0;
        this.nextScheduledMove = 0;
        this.moveAction = true;
        this.scheduleNextMove();
    }

    @Override
    public boolean canUse() {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    /**
     * Do a turn
     */
    public void doTurn() {
        // Set up starting rotation
        if (this.tickCounter == 1) {
            this.squid.setTargetRotPitch(0.0);
            this.squid.setTargetRotPitch(0.0);
        }
        else {
            this.squid.setTargetRotPitch(this.squid.getRotPitch() + (Math.PI / 2.0));
            //this.squid.setTargetRotYaw(this.squid.getRotYaw() + (Math.PI / 2.0));
        }
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 20 + this.r.nextInt(80);
    }

    @Override
    public void tick() {
        ++this.tickCounter;

        if (this.tickCounter == this.nextScheduledMove) {
            if (moveAction) {
                this.doTurn();
            } else {
                this.squid.addForce(this.swimForce);
            }
            this.scheduleNextMove();
            this.moveAction = !this.moveAction;
        }
    }
}
