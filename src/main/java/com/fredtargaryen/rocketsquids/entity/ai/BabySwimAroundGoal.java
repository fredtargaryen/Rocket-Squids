package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BabySwimAroundGoal extends Goal {
    private final BabyRocketSquidEntity squid;

    private final RandomSource r;
    private final double swimForce;
    private int tickCounter;
    private int nextScheduledMove;

    public BabySwimAroundGoal(BabyRocketSquidEntity ebrs, double swimForce) {
        super();
        this.squid = ebrs;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.r = this.squid.getRandom();
        this.swimForce = swimForce;
        this.tickCounter = 0;
        this.nextScheduledMove = 0;
        this.scheduleNextMove();
        //this.currentAngle = 0;
    }

    @Override
    public boolean canUse() {
        return this.squid.isInWater();
    }

    /**
     * Do a turn.
     */
    public void doTurn(boolean blocked) {
        if(blocked) {
            //Just point the opposite way
            Vec3 direction = this.squid.getDirectionAsVec3();
            this.squid.pointToVector(new Vec3(-direction.x, -direction.y, -direction.z), Math.PI / 3.0);
        }
        else {
            //Random doubles between -PI and PI, added to current rotation
            this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
            this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
        }
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 20 + this.r.nextInt(50);
    }

    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Odds:
     * 5/12 - repeats action
     * 7/12 - goes from turning to swimming forward or vice versa
     */
    @Override
    public void tick() {
        ++this.tickCounter;
        double rp = this.squid.getRotPitch();
        double ry = this.squid.getRotYaw();
        //Move and play notes if scheduled
        if(this.tickCounter == this.nextScheduledMove) {
            if(!this.squid.areBlocksInWay()) this.squid.addForce(this.swimForce);
            this.scheduleNextMove();
        }

        double trp = this.squid.getTargRotPitch();
        double Try = this.squid.getTargRotYaw();
        if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
            this.doTurn(this.squid.areBlocksInWay());
        }
    }
}
