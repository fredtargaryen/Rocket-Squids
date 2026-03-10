package com.fredtargaryen.rocketsquids.content.entity.ai;

import com.fredtargaryen.rocketsquids.content.entity.BabyRocketSquidEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BabyFlopAroundGoal extends Goal {
    private final BabyRocketSquidEntity squid;

    public BabyFlopAroundGoal(BabyRocketSquidEntity ebrs) {
        super();
        this.squid = ebrs;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.squid.isInWater();
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
