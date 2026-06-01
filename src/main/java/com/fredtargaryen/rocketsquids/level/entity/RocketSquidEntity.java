// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity;

import com.fredtargaryen.rocketsquids.*;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.config.CommonConfig;
import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import com.fredtargaryen.rocketsquids.level.entity.ai.BlastoffGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.FlopAroundGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.ShakeGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.SwimAroundGoal;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.SquidFireworkMessage;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.fredtargaryen.rocketsquids.DataReference.C_MAJOR_SCALE;
import static com.fredtargaryen.rocketsquids.DataReference.DOUBLE_PI;
import static com.fredtargaryen.rocketsquids.RSDataComponentTypes.SQUELEPORTER;

public class RocketSquidEntity extends AgeableWaterCreature implements Leashable {
    //Properties controlled by the server, but which have a visual effect so need to be synced to clients
    private static final EntityDataAccessor<Double> PITCH_PREV = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH_TARGET = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Double> YAW_PREV = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW_TARGET = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Boolean> SHAKING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> SHAKE_TICKS_REMAINING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> BLASTING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> BLAST_TICKS_REMAINING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BOOLEAN);


    //Server-only properties that don't need to be synced to clients
    /**
     * Server only - temp rider reference in order to throw the rider a certain distance ahead when they dismount during {@link RocketSquidEntity#aiStep}
     */
    private Entity riderToThrow;

    /**
     * Server only - whether the squid is in the process of blasting to a statue
     */
    public boolean blastingToStatue;

    /**
     * Server only - whether the current blast was induced and immediate (e.g. using flint and steel).
     * Causes the rocket squid to explode when the blast ends
     */
    public boolean forcedBlast;

    /**
     * Server only - the three most recent notes heard by the rocket squid
     */
    public int[] latestNotes;

    /**
     * Server only - the three notes a rocket squid needs to hear before it blasts towards a statue
     */
    public int[] targetNotes;

    //Client-only properties that don't need to be synced to the server
    /**
     * Client only - the angle that the rocket squid's tentacles should be at
     */
    public float tentacleAngle;

    /**
     * Client only - follows the value of {@link #tentacleAngle}; used for interpolation each frame
     */
    public float lastTentacleAngle;

    /**
     * Client only - for rotating the player to seat them on the squid's body each frame
     */
    public boolean riderRotated;

    //Unknown if these variables are still needed - check when everything else is done

    /**
     * Server only - how long until the squid can make a baby again
     */
    protected int breedCooldown;

    public RocketSquidEntity(Level level) {
        super(RSEntityTypes.SQUID_TYPE.get(), level);
        this.riderRotated = false;
        this.getLookControl().setLookAt(new Vec3(new Vector3f(Mth.nextFloat(level.getRandom(), 0.0F, 100.0F))));
        RandomSource r = level.getRandom();
        this.latestNotes = new int[]{-1, -1, -1};
        this.targetNotes = new int[]{
                C_MAJOR_SCALE[r.nextInt(7)],
                C_MAJOR_SCALE[r.nextInt(7)],
                C_MAJOR_SCALE[r.nextInt(7)]
        };
    }

    /**
     * Returns the sound this mob makes when it dies.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PITCH_PREV, 0.0);
        builder.define(PITCH, 0.0);
        builder.define(PITCH_TARGET, 0.0);
        builder.define(YAW_PREV, 0.0);
        builder.define(YAW, 0.0);
        builder.define(YAW_TARGET, 0.0);
        builder.define(SHAKING, false);
        builder.define(SHAKE_TICKS_REMAINING, (byte) -1);
        builder.define(BLASTING, false);
        builder.define(BLAST_TICKS_REMAINING, (byte) -1);
        builder.define(SADDLED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlastoffGoal(this));
        this.goalSelector.addGoal(1, new ShakeGoal(this));
        this.goalSelector.addGoal(2, new SwimAroundGoal(this));
        this.goalSelector.addGoal(3, new FlopAroundGoal(this));
    }

    /**
     * Intention is for babies to turn into adults in 5 minutes real-time
     */
    protected int getBabyStartAge() {
        return -6000;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        // This is ran on both the Client and the Server
        // Fraction of distance to target rotation to rotate by each server tick
        double rotateSpeed;
        if (this.isInWater()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9);
            rotateSpeed = 0.06;
        } else {
            Vec3 oldMotion = this.getDeltaMovement();
            double motionX = oldMotion.x;
            double motionY = oldMotion.y;
            double motionZ = oldMotion.z;
            if (this.hurtTime > 0) {
                motionX = 0.0D;
                motionZ = 0.0D;
            }
            if (this.hasEffect(MobEffects.LEVITATION)) {
                motionY += 0.05D * (double) (Objects.requireNonNull(this.getEffect(MobEffects.LEVITATION)).getAmplifier() + 1) - motionY; //levitation
            } else if (!this.isNoGravity()) {
                motionY -= 0.08D;
            }
            motionX *= 0.9800000190734863D;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.9800000190734863D;
            rotateSpeed = 0.15;
            this.setDeltaMovement(motionX, motionY, motionZ);
        }

        boolean onFire = false;
        if (this.isOnFire() || this.isInLava()) {
            onFire = true;
            this.forcedBlast = true;
        }
        if (onFire || this.forcedBlast) {
            this.playSound(RSSounds.BLASTOFF.get(), 0.5F, 1.0F);
            this.getEntityData().set(BLASTING, true);
        }

        //Rotate towards target pitch
        double trp = this.getEntityData().get(PITCH_TARGET);
        double rp = this.getEntityData().get(PITCH);
        if (trp != rp) {
            //Squids rotate <= 180 degrees either way.
            //The squid can rotate out of the interval [-PI, PI].
            rp += (trp - rp) * rotateSpeed;
            this.setPitch(rp);
        }

        //Rotate towards target yaw
        double trY = this.getEntityData().get(YAW_TARGET);
        double ry = this.getEntityData().get(YAW);
        if (trY != ry) {
            ry += (trY - ry) * rotateSpeed;
            this.setYaw(ry);
        }

        Vec3 pos = this.position();

        if (this.level().isClientSide()) {
            //Client side
            //Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if (this.getEntityData().get(BLASTING)) {
                //Tentacles quickly close up
                this.tentacleAngle = 0;
            } else if (this.getEntityData().get(SHAKING)) {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            } else {
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.isInWater() ? (float) ((Math.PI / 6) + (Mth.sin((float) Math.toRadians(4 * (this.tickCount % 360))) * Math.PI / 6)) : 0;
            }
            if (this.getEntityData().get(BLASTING)) {
                if (this.isInWater()) {
                    double smallerX = pos.x - 0.25;
                    double largerX = pos.x + 0.25;
                    double smallerZ = pos.z - 0.25;
                    double largerZ = pos.z + 0.25;
                    this.level().addParticle(ParticleTypes.BUBBLE, smallerX, pos.y, smallerZ, 0.0, 0.0, 0.0);
                    this.level().addParticle(ParticleTypes.BUBBLE, smallerX, pos.y, largerZ, 0.0, 0.0, 0.0);
                    this.level().addParticle(ParticleTypes.BUBBLE, largerX, pos.y, smallerZ, 0.0, 0.0, 0.0);
                    this.level().addParticle(ParticleTypes.BUBBLE, largerX, pos.y, largerZ, 0.0, 0.0, 0.0);
                } else {
                    this.level().addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                }
            }
        } else {
            //Server side
            //Handle breeding ticks
            if (this.breedCooldown > 0) {
                --this.breedCooldown;
            }
            if (this.isInWater() && !this.getEntityData().get(SHAKING) && !this.getEntityData().get(BLASTING)) {
                this.moveToWherePointing();
            }

            // Throw the rider ahead of the squid if they just dismounted
            if (this.riderToThrow != null) {
                Vec3 movement = this.getDeltaMovement().scale(2.5);
                this.riderToThrow.push(movement.x, movement.y, movement.z);
                this.riderToThrow.hurtMarked = true;
                this.riderToThrow = null;
            }
        }
    }

    public double getPreviousPitch() {
        return this.getEntityData().get(PITCH_PREV);
    }

    public double getPitch() {
        return this.getEntityData().get(PITCH);
    }

    public double getTargetPitch() {
        return this.getEntityData().get(PITCH_TARGET);
    }

    private void setPitch(double pitch) {
        this.getEntityData().set(PITCH_PREV, this.getEntityData().get(PITCH));
        this.getEntityData().set(PITCH, pitch);
    }

    public double getPreviousYaw() {
        return this.getEntityData().get(YAW_PREV);
    }

    public double getYaw() {
        return this.getEntityData().get(YAW);
    }

    public double getTargetYaw() {
        return this.getEntityData().get(YAW_TARGET);
    }

    private void setYaw(double yaw) {
        this.getEntityData().set(YAW_PREV, this.getEntityData().get(YAW));
        this.getEntityData().set(YAW, yaw);
    }

    public int getTargetNote(int index) {
        return this.targetNotes[index];
    }

    /**
     * Moves the entity based on strafe, forward (?) and something else
     */
    @Override
    public void travel(@NotNull Vec3 motion) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public @org.jspecify.annotations.Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return RSEntityTypes.SQUID_TYPE.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide()) {
            ItemStack interactStack = player.getItemInHand(hand);
            if (interactStack == ItemStack.EMPTY) {
                if (this.getSaddled() && !this.isVehicle()) {
                    player.startRiding(this);
                    return InteractionResult.SUCCESS;
                }
            } else {
                Item interactItem = interactStack.getItem();
                if (interactItem == RSItems.SQUELEPORTER_INACTIVE.get()) {
                    //The squeleporter is inactive so store the squid here
                    ItemStack newStack = RSItems.SQUELEPORTER_ACTIVE.get().getDefaultInstance();
                    Vec3 pos = player.position();
                    player.level().playSound(null, pos.x, pos.y, pos.z, RSSounds.SQUIDTP_IN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    // Set squid data
                    TagValueOutput vo = TagValueOutput.createWithoutContext(null);
                    this.save(vo);
                    newStack.set(SQUELEPORTER, new SqueleporterData(vo.buildResult()));
                    this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
                    EquipmentSlot handEquip = EquipmentSlot.MAINHAND;
                    if (hand == InteractionHand.OFF_HAND) {
                        handEquip = EquipmentSlot.OFFHAND;
                    }
                    player.setItemSlot(handEquip, newStack);
                    player.getCooldowns().addCooldown(newStack, 10);
                } else if (interactItem == Items.FLINT_AND_STEEL) {
                    // if the player isn't in creative we damage the flint and steel
                    if (!player.isCreative()) {
                        interactStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                    }
                    this.forcedBlast = true;
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.SADDLE) {
                    if (!this.getSaddled()) {
                        interactStack.consume(1, player);
                        this.setSaddled(true);
                    }
                    player.startRiding(this);
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.FEATHER) {
                    this.getEntityData().set(SHAKING, true);
                    return InteractionResult.SUCCESS;
                } else {
                    if (this.getSaddled() && !this.isVehicle()) {
                        player.startRiding(this);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    /**
     * Called when the squid explodes to create particle effects, items and to remove the entity.
     */
    public void explode() {
        if (!this.level().isClientSide()) {
            Vec3 pos = this.position();
            if (CommonConfig.ROCKET_SQUID_EXPLOSIONS_DESTROY) {
                this.level().explode(this, pos.x, pos.y, pos.z, 2.5F, Level.ExplosionInteraction.TNT);
            } else {
                this.level().explode(this, pos.x, pos.y, pos.z, 2.5F, Level.ExplosionInteraction.MOB);
            }

            int noSacs = 3 + this.random.nextInt(3);
            for (int x = 0; x < noSacs; x++) {
                ItemEntity entityitem = new ItemEntity(this.level(), pos.x, pos.y, pos.z, new ItemStack(RSItems.NITRO_SAC.get()));
                double motionX = this.random.nextDouble() * 1.5F * (this.random.nextBoolean() ? 1 : -1);
                double motionY = -0.2;
                double motionZ = this.random.nextDouble() * 1.5F * (this.random.nextBoolean() ? 1 : -1);
                entityitem.setDeltaMovement(motionX, motionY, motionZ);
                this.level().addFreshEntity(entityitem);
            }

            int noTubes = 2 + this.random.nextInt(3);
            for (int x = 0; x < noTubes; x++) {
                ItemEntity entityitem = new ItemEntity(this.level(), pos.x, pos.y, pos.z, new ItemStack(RSItems.TURBO_TUBE.get()));
                double motionX = this.random.nextDouble() * 1.5F * (this.random.nextBoolean() ? 1 : -1);
                double motionY = -0.2;
                double motionZ = this.random.nextDouble() * 1.5F * (this.random.nextBoolean() ? 1 : -1);
                entityitem.setDeltaMovement(motionX, motionY, motionZ);
                this.level().addFreshEntity(entityitem);
            }

            // tell the client to create particles
            MessageHandler.sendToPlayersNear((ServerLevel) this.level(), new SquidFireworkMessage(this.getUUID()), pos.x, pos.y, pos.z, 64);
        }
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        }
        super.remove(reason);
    }

    /**
     * Spawns the squid firework particles for when they explode.
     */
    public void doFireworkParticles() {
        ParticleEngine effectRenderer = Minecraft.getInstance().particleEngine;
        Vec3 pos = this.position();
        effectRenderer.add(new SquidFireworkParticle.SquidStarter((ClientLevel) this.level(), pos.x, pos.y, pos.z, effectRenderer));
    }

    /**
     * Spawns tells the client to spawn heart particles above the head of the rocket squids, used for when they "breed".
     *
     * @param level The level the rocket squid is in as represented on the server.
     */
    public void spawnHearts(ServerLevel level) {
        if (!this.level().isClientSide()) {
            Vec3 thisPos = this.position();
            level.sendParticles(ParticleTypes.HEART.getType(), thisPos.x, thisPos.y + 1.5D, thisPos.z, 3, 0.25D, 0.0D, 0.25D, 1.0D);
        }
    }

    /**
     * Applies a velocity to the entities (unless they're riding), to push them away from each other.
     *
     * @param obstacle The Entity that is colliding with the rocket squid.
     */
    public void push(@NotNull Entity obstacle) {
        Entity passenger = this.getFirstPassenger();
        if (passenger != obstacle) {
            // Obstacle is not the rider, so apply collision
            if (!obstacle.noPhysics && !this.noPhysics) {
                Vec3 thisPos = this.position();
                if (!this.level().isClientSide() && obstacle.getType() == RSEntityTypes.SQUID_TYPE.get()) {
                    RocketSquidEntity partner = (RocketSquidEntity) obstacle;
                    if (this.canBreed() && partner.canBreed()) {
                        // if it is another rocket squid that can breed we run the breed method
                        this.breed(partner);
                    }
                }
                Vec3 obstaclePos = obstacle.position();
                double xDist = obstaclePos.x - thisPos.x;
                double zDist = obstaclePos.z - thisPos.z;
                double yDist = obstaclePos.y - thisPos.y;
                double largerDist = Mth.absMax(xDist, Mth.absMax(xDist, zDist));

                if (largerDist >= 0.009999999776482582D) {
                    largerDist = Math.sqrt(largerDist);
                    xDist /= largerDist;
                    yDist /= largerDist;
                    zDist /= largerDist;
                    double d3 = 1.0D / largerDist;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    xDist *= d3;
                    yDist *= d3;
                    zDist *= d3;
                    // magic numbers
                    xDist *= 0.05000000074505806D;
                    yDist *= 0.05000000074505806D;
                    zDist *= 0.05000000074505806D;

                    this.push(-xDist * 0.02, -yDist * 0.02, -zDist * 0.02);
                    obstacle.push(xDist * 0.98, yDist * 0.98, zDist * 0.98);
                }
            }
        }
    }

    public boolean canBreed() {
        return !this.isBaby() && this.breedCooldown <= 0 && !this.hasPassengers();
    }

    @SuppressWarnings("unused")
    public boolean isFood(ItemStack stack) {
        Item stackItem = stack.getItem();
        return stackItem == Items.COD || stackItem == Items.SALMON || stackItem == Items.TROPICAL_FISH || stackItem == Items.GUNPOWDER;
    }

    /**
     * A squid will be pointing 1-3 directions at a time.
     *
     * @return Whether a solid block is in the way in all directions pointed, so the squid can't move much
     */
    @SuppressWarnings("deprecation")
    public boolean areBlocksInWay() {
        BlockPos squidPos = this.blockPosition();
        for (Direction dir : this.getDirectionsPointing()) {
            if (!this.level().getBlockState(squidPos.relative(dir)).isSolid()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Direction> getDirectionsPointing() {
        ArrayList<Direction> directions = new ArrayList<>();
        Vec3 direction = RotationHelper.getSquidDirection(this);
        double t = RotationHelper.DIRECTION_POINT_THRESHOLD;
        if (direction.y > t) {
            directions.add(Direction.UP);
        } else if (direction.y < -t) {
            directions.add(Direction.DOWN);
        }
        //South is positive z I think
        if (direction.z > t) {
            directions.add(Direction.SOUTH);
        } else if (direction.z < -t) {
            directions.add(Direction.NORTH);
        }
        //East is positive x I think
        if (direction.x > t) {
            directions.add(Direction.EAST);
        } else if (direction.x < -t) {
            directions.add(Direction.WEST);
        }
        return directions;
    }

    public void addForce(double force) {
        if (!this.level().isClientSide()) {
            Vec3 motion = this.getDeltaMovement();
            Vec3 direction = RotationHelper.getSquidDirection(this);
            this.setDeltaMovement(
                    motion.x + direction.x * force,
                    motion.y + direction.y * force,
                    motion.z + direction.z * force);
            this.setOnGround(false);
        }
    }

    /**
     * Get the current force, and recalculate the motion based on the current angle of the squid
     */
    public void moveToWherePointing() {
        Vec3 motion = this.getDeltaMovement();
        double force = Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);
        Vec3 direction = RotationHelper.getSquidDirection(this);
        this.setDeltaMovement(
                direction.x * force,
                direction.y * force,
                direction.z * force);
        this.setOnGround(false);
    }

    /**
     * Set the rotation so that the squid is pointing along the desired direction vector
     *
     * @param vec       normalised vector representing intended squid direction
     * @param deviation random addition to the angles so it doesn't look too perfect
     */
    public void pointToVector(Vec3 vec, double deviation) {
        double hozDir = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        this.setTargetYaw(Math.acos(vec.z / hozDir) + deviation * (this.random.nextBoolean() ? 1 : -1));
        this.setTargetPitch(Math.acos(vec.y) + deviation * (this.random.nextBoolean() ? 1 : -1));
    }

    /**
     * Turn the entity based on its motion vector
     */
    public void pointToWhereMoving() {
        Vec3 motion = this.getDeltaMovement();
        if (!(Math.abs(motion.y) < 0.0785 && motion.x == 0.0 && motion.z == 0.0)) {
            //The aim is to find the local z movement to decide if the squid should pitch backwards or forwards.
            //The global z movement is given by this.motionZ.
            //In addForce, this.motionZ is given by horizontalForce * cos(yaw).
            //By rearranging, horizontalForce = this.motionZ / cos(yaw).
            //This is the amount by which the squid is moving along its own z axis (forwards or backwards).
            double speed = motion.z / Math.cos(this.getEntityData().get(YAW));
            this.setTargetPitch(Math.PI / 2 - Math.atan2(motion.y, speed));
        }
    }

    /**
     * Run when adult rocket squids try to "breed".
     *
     * @param partner The potential partner to "breed" with.
     */
    private void breed(RocketSquidEntity partner) {
        if (!this.level().isClientSide()) {
            Vec3 thisPos = this.position();
            // get the UUID of the "partner" rocket squid
            UUID partnerUUID = partner.getUUID();
            switch (this.uuid.compareTo(partnerUUID)) {
                // run .compareTo() on it to find which squid has the greater UUID
                case 1:
                    // the one with the greater UUID creates the child
                    this.spawnChildFromBreeding((ServerLevel) this.level(), partner);
                    this.spawnHearts((ServerLevel) this.level());
                    break;
                case -1:
                    // the one with the lesser UUID doesn't
                    this.breedCooldown = CommonConfig.BREED_COOLDOWN;
                    this.spawnHearts((ServerLevel) this.level());
                    break;
                default:
                    // Probably impossible...
                    this.forcedBlast = true;
                    partner.forcedBlast = true;
            }
        }
    }

    //////////////////
    //RIDING METHODS//

    /// ///////////////

    @Override
    protected void addPassenger(@NotNull Entity p) {
        if (this.getPassengers().isEmpty()) {
            super.addPassenger(p);
            if (this.level().isClientSide()) {
                NeoForge.EVENT_BUS.register(this);
            }
        }
    }

    /**
     * Keep this null because Rocket Squids are not "controlled" by the usual means
     */
    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public boolean hasPassengers() {
        return !this.getPassengers().isEmpty();
    }

    /**
     * Checks if squid pitch is less than -45 degrees and more than -135 degrees.
     * Between these angles the player would appear to hit the ground first so the player should be hurt.
     */
    @Override
    public boolean causeFallDamage(double distance, float damageMultiplier, @NotNull DamageSource damageSource) {
        if (this.isVehicle()) {
            if (Math.sin(this.getEntityData().get(PITCH)) < -0.7071067811865) {
                for (Entity entity : this.getPassengers()) {
                    entity.causeFallDamage(distance, damageMultiplier, damageSource);
                }
            }
        }
        return false;
    }

    @Override
    protected void removePassenger(@NotNull Entity passenger) {
        super.removePassenger(passenger);
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        } else if (this.getEntityData().get(BLASTING)) {
            this.riderToThrow = passenger;
        }
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn) {
        return !this.isBaby();
    }

    public boolean getShaking() {
        return this.getEntityData().get(SHAKING);
    }

    public void setShaking(boolean shaking) {
        this.getEntityData().set(SHAKING, shaking);
    }

    public byte getShakeTicks() {
        return this.getEntityData().get(SHAKE_TICKS_REMAINING);
    }

    public void setShakeTicks(byte shakeTicks) {
        this.getEntityData().set(SHAKE_TICKS_REMAINING, shakeTicks);
    }

    public boolean getBlasting() {
        return this.getEntityData().get(BLASTING);
    }

    public void setBlasting(boolean blasting) {
        this.getEntityData().set(BLASTING, blasting);
    }

    public boolean getSaddled() {
        return this.getEntityData().get(SADDLED);
    }

    /**
     * Set or remove the saddle of the squid.
     */
    private void setSaddled(boolean saddled) {
        this.getEntityData().set(SADDLED, saddled);
    }

    /**
     * dropEquipment
     */
    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        if (this.getSaddled()) {
            this.spawnAtLocation(level, Items.SADDLE);
        }
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.isBaby();
    }

    /////////////////
    //CLIENT EVENTS//
    /////////////////
    /**
     * Add transformations to put player on back of squid.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void addRotation(RenderPlayerEvent.Pre event) {
        UUID uuid = event.getRenderState().getRenderData(ClientHandler.PLAYER_ID);
        if (this.getFirstPassenger().getUUID().equals(uuid)) {
            float partialTick = event.getPartialTick();

            SynchedEntityData data = this.getEntityData();
            double prevPitchRads = data.get(PITCH_PREV);
            double pitchRads = data.get(PITCH);
            double exactPitchRads = prevPitchRads + (pitchRads - prevPitchRads) * partialTick;
            double squidAngle = exactPitchRads - (Math.PI / 2.0);

            double prevYawRads = data.get(YAW_PREV);
            double yawRads = data.get(YAW);
            double exactYawRads = prevYawRads + (yawRads - prevYawRads) * partialTick;

            double translation = -0.2 * Math.abs(Math.sin(squidAngle / 2.0));

            this.riderRotated = true;
            PoseStack stack = event.getPoseStack();
            stack.pushPose();
            // Rotate the rider to match the squid's rotation
            Quaternionf quat = new Quaternionf()
                    .rotateLocalX((float) (squidAngle))
                    .rotateLocalY((float) -exactYawRads);
            stack.mulPose(quat);
            // Keep the rider from floating away from the saddle
            stack.translate(0.0, translation, 0.0);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void popRotation(RenderPlayerEvent.Post event) {
        if (this.riderRotated) {
            event.getPoseStack().popPose();
            this.riderRotated = false;
        }
    }

    ///////
    //NBT//

    /// ////
    @Override
    public void addAdditionalSaveData(ValueOutput vo) {
        super.addAdditionalSaveData(vo);
        vo.putBoolean("Saddle", this.getSaddled());
        vo.putInt("Breed Cooldown", this.breedCooldown);
        vo.putDouble("pitch", this.getEntityData().get(PITCH));
        vo.putDouble("targetPitch", this.getEntityData().get(PITCH_TARGET));
        vo.putDouble("yaw", this.getEntityData().get(YAW));
        vo.putDouble("targetYaw", this.getEntityData().get(YAW_TARGET));
        vo.putBoolean("shaking", this.getEntityData().get(SHAKING));
        vo.putBoolean("blasting", this.getEntityData().get(BLASTING));
        vo.putBoolean("forcedblast", this.forcedBlast);
        vo.putIntArray("latestnotes", this.latestNotes);
        vo.putIntArray("targetnotes", this.targetNotes);
        vo.putBoolean("blasttostatue", this.blastingToStatue);
    }

    @Override
    public void readAdditionalSaveData(ValueInput vi) {
        super.readAdditionalSaveData(vi);
        //Force comes from reading motion tags
        this.setSaddled(vi.getBooleanOr("Saddle", false));
        this.breedCooldown = vi.getShortOr("Breed Cooldown", (short) 0);
        this.getEntityData().set(PITCH, vi.getDoubleOr("pitch", 0.0));
        this.getEntityData().set(YAW, vi.getDoubleOr("yaw", 0.0));
        this.getEntityData().set(PITCH_TARGET, vi.getDoubleOr("targetPitch", 0.0));
        this.getEntityData().set(YAW_TARGET, vi.getDoubleOr("targetYaw", 0.0));
        this.getEntityData().set(SHAKING, vi.getBooleanOr("shaking", false));
        this.getEntityData().set(BLASTING, vi.getBooleanOr("blasting", false));
        this.forcedBlast = vi.getBooleanOr("forcedblast", false);
        this.setLatestNotes(vi.getIntArray("latestnotes").orElse(new int[]{-1, -1, -1}));
        this.targetNotes = vi.getIntArray("targetnotes").orElse(new int[]{-1, -1, -1});
        this.blastingToStatue = vi.getBooleanOr("blasttostatue", false);
    }

    public void forcePitchInstant(double pitch) {
        this.getEntityData().set(PITCH, pitch);
        this.getEntityData().set(PITCH_TARGET, pitch);
    }

    public void forceYawInstant(double yaw) {
        this.getEntityData().set(YAW, yaw);
        this.getEntityData().set(YAW_TARGET, yaw);
    }

    public void setTargetPitch(double p) {
        double currentPitch = this.getEntityData().get(PITCH);
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while (currentPitch < -Math.PI) {
            currentPitch += DOUBLE_PI;
        }
        while (p < -Math.PI) {
            p += DOUBLE_PI;
        }
        while (currentPitch > Math.PI) {
            currentPitch -= DOUBLE_PI;
        }
        while (p > Math.PI) {
            p -= DOUBLE_PI;
        }
        this.getEntityData().set(PITCH_PREV, currentPitch);
        this.getEntityData().set(PITCH, currentPitch);
        this.getEntityData().set(PITCH_TARGET, p);
    }

    public void setTargetYaw(double y) {
        double currentYaw = this.getEntityData().get(YAW);
        //Set current rotation to be within [-PI, PI].
        //Any operations on current rotation are also applied to target rotation.
        //Target rotation can be outside the interval; it will be
        //current rotation and brought back in next time this method is called.
        while (currentYaw < -Math.PI) {
            currentYaw += DOUBLE_PI;
        }
        while (y < -Math.PI) {
            y += DOUBLE_PI;
        }
        while (currentYaw > Math.PI) {
            currentYaw -= DOUBLE_PI;
        }
        while (y > Math.PI) {
            y -= DOUBLE_PI;
        }
        this.getEntityData().set(YAW_PREV, currentYaw);
        this.getEntityData().set(YAW, currentYaw);
        this.getEntityData().set(YAW_TARGET, y);
    }

    public void setLatestNotes(int[] ln) {
        if (ln.length != 3) {
            RocketSquidsBase.LOGGER.error("Tried to set a latest notes value of incorrect length; resetting to default");
            this.latestNotes = new int[]{-1, -1, -1};
            return;
        }
        this.latestNotes = ln;
    }

    public void processNote(int note) {
        this.latestNotes[0] = this.latestNotes[1];
        this.latestNotes[1] = this.latestNotes[2];
        this.latestNotes[2] = note;
        if (this.latestNotes[0] == this.targetNotes[0]
                && this.latestNotes[1] == this.targetNotes[1]
                && this.latestNotes[2] == this.targetNotes[2]) {
            this.blastingToStatue = true;
        }
    }

    public void resetBreedCooldown() {
        this.breedCooldown = CommonConfig.BREED_COOLDOWN;
    }

    public void spawnChildFromBreeding(ServerLevel level, RocketSquidEntity partner) {
        AgeableMob offspring = this.getBreedOffspring(level, partner);
        final net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent event = new net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent(this, partner, offspring);
        final boolean cancelled = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled();
        offspring = event.getChild();
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.setAge(6000);
            partner.setAge(6000);
            this.resetBreedCooldown();
            partner.resetBreedCooldown();
            return;
        }
        if (offspring != null) {
            offspring.setBaby(true);
            offspring.snapTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.finalizeSpawnChildFromBreeding(level, partner, offspring);
            level.addFreshEntityWithPassengers(offspring);
        }
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel level, RocketSquidEntity partner, @org.jspecify.annotations.Nullable AgeableMob offspring) {
        this.setAge(6000);
        partner.setAge(6000);
        this.resetBreedCooldown();
        partner.resetBreedCooldown();
        level.broadcastEntityEvent(this, (byte) 18);
    }
}
