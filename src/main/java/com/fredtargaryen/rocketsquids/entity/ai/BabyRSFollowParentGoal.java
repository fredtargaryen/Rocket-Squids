package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.BabyRocketSquidEntity;
import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class BabyRSFollowParentGoal extends Goal {
    private final BabyRocketSquidEntity baby;
    private Entity parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public BabyRSFollowParentGoal(BabyRocketSquidEntity baby, double d) {
        this.baby = baby;
        this.speedModifier = d;
    }

    @Override
    public boolean canUse() {
        if (!this.baby.isBaby()) {
            return false;
        } else {
            List<Entity> list = this.baby.level.getEntitiesOfClass(RocketSquidEntity.class, this.baby.getBoundingBox().inflate(8.0, 4.0, 8.0));
            Entity rocketSquid = null;
            double d = Double.MAX_VALUE;

            for (Entity rocketSquidEntry : list) {
                double e = this.baby.distanceToSqr(rocketSquidEntry);
                if (!(e > d)) {
                    d = e;
                    rocketSquid = rocketSquidEntry;
                }
            }

            if (rocketSquid == null) {
                return false;
            } else if (d < 9.0) {
                return false;
            } else {
                this.parent = rocketSquid;
                return true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.baby.isBaby()) {
            return false;
        } else if (!this.parent.isAlive()) {
            return false;
        } else {
            double d = this.baby.distanceToSqr(this.parent);
            return !(d < 9.0) && !(d > 256.0);
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.baby.getNavigation().moveTo(this.parent, this.speedModifier);
        }
    }
}
