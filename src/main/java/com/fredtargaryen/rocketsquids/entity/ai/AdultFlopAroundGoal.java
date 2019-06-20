package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AdultFlopAroundGoal extends Goal {
    private final RocketSquidEntity squid;

    public AdultFlopAroundGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        return !this.squid.isInWater() && !this.squid.isInLava() && !this.squid.getBlasting();
    }

    @Override
    public void tick() {
        if (this.squid.onGround) {
            this.squid.setTargetRotPitch(Math.PI / 2);
        } else if(Math.abs(this.squid.getMotion().y) > 0.008){
            this.squid.pointToWhereFlying();
        }
    }
}
