package com.fredtargaryen.rocketsquids.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class AbstractSquidEntity extends WaterAnimal {
    protected boolean newPacketRequired;

    ///////////////
    //CLIENT ONLY//
    ///////////////
    public float tentacleAngle;
    public float lastTentacleAngle;

    public AbstractSquidEntity(EntityType<? extends AbstractSquidEntity> type, Level world) {
        super(type, world);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource ds)
    {
        return null;
    }

    /**
     * Returns the sound this mob makes when it dies.
     */
    @Override
    protected SoundEvent getDeathSound()
    {
        return null;
    }

    public boolean canBreed() {
        return false;
    }

    public boolean isFood(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem == Items.COD || stackItem == Items.SALMON || stackItem == Items.TROPICAL_FISH;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    /**
     * A squid will be pointing 1-3 directions at a time.
     * @return Whether a solid block is in the way in all directions pointed, so the squid can't move much
     */
    public boolean areBlocksInWay() {
        BlockPos squidPos = this.blockPosition();
        for(Direction dir : this.getDirectionsPointing()) {
            if(!this.level.getBlockState(squidPos.relative(dir)).getMaterial().isSolid()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Direction> getDirectionsPointing() {
        ArrayList<Direction> directions = new ArrayList<>();
        Vec3 direction = this.getDirectionAsVec3();
        //A threshold; if a component is beyond this the squid is considered pointing in that direction
        double t = 0.45;//0.3125;
        if(direction.y > t) {
            directions.add(Direction.UP);
        }
        else if(direction.y < -t) {
            directions.add(Direction.DOWN);
        }
        //South is positive z I think
        if(direction.z > t) {
            directions.add(Direction.SOUTH);
        }
        else if(direction.z < -t) {
            directions.add(Direction.NORTH);
        }
        //East is positive x I think
        if(direction.x > t) {
            directions.add(Direction.EAST);
        }
        else if(direction.x < -t) {
            directions.add(Direction.WEST);
        }
        return directions;
    }

    public Vec3 getDirectionAsVec3() {
        double rp = this.getRotPitch();
        double ry = this.getRotYaw();
        double yDir = Math.cos(rp);
        double hozDir = Math.sin(rp);
        double zDir = hozDir * Math.cos(ry);
        double xDir = hozDir * -Math.sin(ry);
        return new Vec3(xDir, yDir, zDir);
    }

    public void addForce(double force) {
        if(!this.level.isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            Vec3 direction = this.getDirectionAsVec3();
            this.setDeltaMovement(
                    motion.x + direction.x * force,
                    motion.y + direction.y * force,
                    motion.z + direction.z * force);
            this.onGround = false;
        }
    }

    /**
     * Get the current force, and recalculate the motion based on the current angle of the squid
     */
    public void moveToWherePointing() {
        Vec3 motion = this.getDeltaMovement();
        double force = Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);
        Vec3 direction = this.getDirectionAsVec3();
        this.setDeltaMovement(
                direction.x * force,
                direction.y * force,
                direction.z * force);
        this.onGround = false;
    }

    /**
     * Set the rotation so that the squid is pointing along the desired direction vector
     * @param vec normalised vector representing intended squid direction
     * @param deviation random addition to the angles so it doesn't look too perfect
     */
    public void pointToVector(Vec3 vec, double deviation) {
        double hozDir = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        this.setTargetRotYaw(Math.acos(vec.z / hozDir) + deviation * (this.random.nextBoolean() ? 1 : -1));
        this.setTargetRotPitch(Math.acos(vec.y) + deviation * (this.random.nextBoolean() ? 1 : -1));
    }

    /**
     * Turn the entity based on its motion vector
     */
    public void pointToWhereMoving() {
        Vec3 motion = this.getDeltaMovement();
        if(!(Math.abs(motion.y) < 0.0785 && motion.x == 0.0 && motion.z == 0.0)) {
            //The aim is to find the local z movement to decide if the squid should pitch backwards or forwards.
            //The global z movement is given by this.motionZ.
            //In addForce, this.motionZ is given by horizontalForce * cos(yaw).
            //By rearranging, horizontalForce = this.motionZ / cos(yaw).
            //This is the amount by which the squid is moving along its own z axis (forwards or backwards).
            double speed = motion.z / Math.cos(this.getRotYaw());
            this.setTargetRotPitch(Math.PI / 2 - Math.atan2(motion.y, speed));
        }
    }

    /////////////////////////////
    //CAPABILITY ROTATION STUFF//
    /////////////////////////////
    public abstract double getRotPitch();
    public abstract double getRotYaw();
    public abstract void setTargetRotPitch(double target);
    public abstract void setTargetRotYaw(double target);
}
