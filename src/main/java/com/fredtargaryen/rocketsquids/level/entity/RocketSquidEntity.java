// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity;

import com.fredtargaryen.rocketsquids.*;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import com.fredtargaryen.rocketsquids.config.CommonConfig;
import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import com.fredtargaryen.rocketsquids.level.entity.ai.*;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.SquidFireworkMessage;
import com.fredtargaryen.rocketsquids.network.message.TrickMessage;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import com.fredtargaryen.rocketsquids.util.ValueIOHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

import static com.fredtargaryen.rocketsquids.DataReference.C_MAJOR_SCALE;
import static com.fredtargaryen.rocketsquids.RSDataComponentTypes.SQUELEPORTER;
import static com.fredtargaryen.rocketsquids.util.RotationHelper.DOUBLE_PI;

public class RocketSquidEntity extends AgeableWaterCreature implements Leashable {
    //Properties controlled by the server, but which have a visual effect so need to be synced to clients
    private static final EntityDataAccessor<Double> PITCH_PREV = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> PITCH_TARGET = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Double> YAW_PREV = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> YAW_TARGET = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Double> ROLL_PREV = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> ROLL = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());
    private static final EntityDataAccessor<Double> ROLL_TARGET = SynchedEntityData.defineId(RocketSquidEntity.class, RSEntityDataSerializers.DOUBLE.get());

    private static final EntityDataAccessor<Boolean> SHAKING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Byte> COUNTDOWN_TICKS = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> BLAST_TICKS_REMAINING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> TRICK_TICKS_REMAINING = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BYTE);

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

    /**
     * Server only - parameters for doing a trick, set by a message to the server if a player is riding, or occasionally by the squid itself if it chooses to do a trick
     */
    public TrickParameters trickParams;


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

    public RocketSquidEntity(Level level) {
        super(RSEntityTypes.SQUID_TYPE.get(), level);
        this.riderRotated = false;
        this.forcePitchInstant(Mth.nextDouble(level.getRandom(), -Math.PI, Math.PI));
        this.forceYawInstant(Mth.nextDouble(level.getRandom(), -Math.PI, Math.PI));
        this.forceRollInstant(Mth.nextDouble(level.getRandom(), -Math.PI, Math.PI));
        RandomSource r = level.getRandom();
        this.latestNotes = new int[]{-1, -1, -1};
        this.targetNotes = new int[]{
                C_MAJOR_SCALE[r.nextInt(7)],
                C_MAJOR_SCALE[r.nextInt(7)],
                C_MAJOR_SCALE[r.nextInt(7)]
        };
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D);
    }

    // SECTION FOR DATA SYNCING
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PITCH_PREV, 0.0);
        builder.define(PITCH, 0.0);
        builder.define(PITCH_TARGET, 0.0);
        builder.define(YAW_PREV, 0.0);
        builder.define(YAW, 0.0);
        builder.define(YAW_TARGET, 0.0);
        builder.define(ROLL_PREV, 0.0);
        builder.define(ROLL, 0.0);
        builder.define(ROLL_TARGET, 0.0);
        builder.define(SHAKING, false);
        builder.define(COUNTDOWN_TICKS, (byte) 0);
        builder.define(BLAST_TICKS_REMAINING, (byte) 0);
        builder.define(TRICK_TICKS_REMAINING, (byte) 0);
        builder.define(SADDLED, false);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput vo) {
        super.addAdditionalSaveData(vo);
        SynchedEntityData sed = this.getEntityData();
        vo.putDouble("CurrentPitch", sed.get(PITCH));
        vo.putDouble("TargetPitch", sed.get(PITCH_TARGET));
        vo.putDouble("CurrentYaw", sed.get(YAW));
        vo.putDouble("TargetYaw", sed.get(YAW_TARGET));
        vo.putDouble("CurrentRoll", sed.get(ROLL));
        vo.putDouble("TargetRoll", sed.get(ROLL_TARGET));
        vo.putBoolean("Shaking", sed.get(SHAKING));
        vo.putByte("CountdownTicks", sed.get(COUNTDOWN_TICKS));
        vo.putByte("BlastTicksRemaining", sed.get(BLAST_TICKS_REMAINING));
        vo.putByte("TrickTicksRemaining", sed.get(TRICK_TICKS_REMAINING));
        vo.putBoolean("Saddled", sed.get(SADDLED));
        vo.putBoolean("BlastingToStatue", this.blastingToStatue);
        vo.putBoolean("ForcedBlast", this.forcedBlast);
        vo.putIntArray("LatestNotes", this.latestNotes);
        vo.putIntArray("TargetNotes", this.targetNotes);
    }

    @Override
    public void readAdditionalSaveData(ValueInput vi) {
        super.readAdditionalSaveData(vi);
        SynchedEntityData sed = this.getEntityData();
        sed.set(PITCH, vi.getDoubleOr("CurrentPitch", 0.0));
        sed.set(PITCH_TARGET, vi.getDoubleOr("TargetPitch", 0.0));
        sed.set(YAW, vi.getDoubleOr("CurrentYaw", 0.0));
        sed.set(YAW_TARGET, vi.getDoubleOr("TargetYaw", 0.0));
        sed.set(ROLL, vi.getDoubleOr("CurrentRoll", 0.0));
        sed.set(ROLL_TARGET, vi.getDoubleOr("TargetRoll", 0.0));
        sed.set(SHAKING, vi.getBooleanOr("Shaking", false));
        sed.set(COUNTDOWN_TICKS, vi.getByteOr("CountdownTicks", (byte) 0));
        sed.set(BLAST_TICKS_REMAINING, vi.getByteOr("BlastTicksRemaining", (byte) 0));
        sed.set(BLAST_TICKS_REMAINING, vi.getByteOr("TrickTicksRemaining", (byte) 0));
        sed.set(SADDLED, vi.getBooleanOr("Saddled", false));
        this.blastingToStatue = vi.getBooleanOr("BlastingToStatue", false);
        this.forcedBlast = vi.getBooleanOr("ForcedBlast", false);
        this.setLatestNotes(vi.getIntArray("LatestNotes").orElse(new int[]{-1, -1, -1}));
        this.targetNotes = vi.getIntArray("TargetNotes").orElse(new int[]{-1, -1, -1});
    }

    // SECTION FOR AI, NAVIGATION AND INTERACTION
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TrickGoal(this));
        this.goalSelector.addGoal(1, new BlastToStatueGoal(this));
        this.goalSelector.addGoal(2, new BlastoffGoal(this));
        this.goalSelector.addGoal(3, new CountdownGoal(this));
        this.goalSelector.addGoal(4, new SingGoal(this));
        this.goalSelector.addGoal(5, new SwimAroundGoal(this));
        this.goalSelector.addGoal(6, new FlopAroundGoal(this));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        // This is run on both the Client and the Server
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

        if (this.getBlasting()) rotateSpeed = 0.25;

        if (this.isOnFire() || this.isInLava()) {
            this.forcedBlast = true;
            this.blastoff();
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

        //Rotate towards target roll
        double trr = this.getEntityData().get(ROLL_TARGET);
        double rr = this.getEntityData().get(ROLL);
        if (trr != rr) {
            rr += (trr - rr) * rotateSpeed;
            this.setRoll(rr);
        }

        if (this.level().isClientSide()) {
            // Client side
            // Have to roll (haha) our own vehicle-jumping mechanics because vanilla vehicle-jumping relies on being
            // fully in control of the squid
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && this.getFirstPassenger() == player) {
                if (player.input.keyPresses.jump() && this.getBlasting() && !this.getTricking()) {
                    // Do a trick!
                    MessageHandler.sendToServer(new TrickMessage(this.uuid, TrickParameters.createFromClientInput(player.input)));
                }
            }
            // Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if (this.getBlasting()) {
                //Tentacles quickly close up
                this.tentacleAngle = 0;
            } else if (this.getEntityData().get(SHAKING)) {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            } else {
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.isInWater() ? (float) ((Math.PI / 6) + (Mth.sin((float) Math.toRadians(4 * (this.tickCount % 360))) * Math.PI / 6)) : 0;
            }
            if (this.getBlasting()) {
                Vec3 pos = this.position();
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
            // Throw the rider ahead of the squid if they just dismounted
            if (this.riderToThrow != null) {
                Vec3 movement = this.getDeltaMovement().scale(2.5);
                this.riderToThrow.push(movement.x, movement.y, movement.z);
                this.riderToThrow.hurtMarked = true;
                this.riderToThrow = null;
            }
        }
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
                    TagValueOutput vo = ValueIOHelper.getNewEmptyCompoundTagAsValueOutput();
                    this.save(vo);
                    newStack.set(SQUELEPORTER, new SqueleporterData(vo.buildResult()));
                    this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
                    EquipmentSlot handEquip = EquipmentSlot.MAINHAND;
                    if (hand == InteractionHand.OFF_HAND) {
                        handEquip = EquipmentSlot.OFFHAND;
                    }
                    player.setItemSlot(handEquip, newStack);
                    player.getCooldowns().addCooldown(newStack, 10);
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.FLINT_AND_STEEL) {
                    // if the player isn't in creative we damage the flint and steel
                    if (!player.isCreative()) {
                        interactStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                    }
                    this.forcedBlast = true;
                    this.blastoff();
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.SADDLE) {
                    if (!this.getSaddled()) {
                        interactStack.consume(1, player);
                        this.setSaddled(true);
                    }
                    player.startRiding(this);
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.FEATHER) {
                    this.beginCountdown();
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
    public void travel(@NotNull Vec3 motion) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /**
     * A squid will be pointing 1-3 directions at a time.
     *
     * @return Whether a solid block is in the way in all directions pointed, so the squid can't move much
     */
    @SuppressWarnings("deprecation")
    public boolean areBlocksInWay() {
        BlockPos squidPos = this.blockPosition();
        for (Direction dir : RotationHelper.getBlockDirectionsSquidIsPointing(this)) {
            if (!this.level().getBlockState(squidPos.relative(dir)).isSolid()) {
                return false;
            }
        }
        return true;
    }

    public void addForce(double force) {
        if (!this.level().isClientSide()) {
            Vec3 direction = RotationHelper.getSquidDirection(this);
            this.addDeltaMovement(direction.scale(force));
            this.setOnGround(false);
        }
    }

    // SECTION FOR SIMPLE PROPERTY MANIPULATION
    public double getPreviousPitch() {
        return this.getEntityData().get(PITCH_PREV);
    }

    public double getPitch() {
        return this.getEntityData().get(PITCH);
    }

    private void setPitch(double pitch) {
        this.getEntityData().set(PITCH_PREV, this.getEntityData().get(PITCH));
        this.getEntityData().set(PITCH, pitch);
    }

    public void forcePitchInstant(double pitch) {
        pitch = RotationHelper.resetRotationValueWithinRange(pitch);
        this.getEntityData().set(PITCH_PREV, pitch);
        this.getEntityData().set(PITCH, pitch);
        this.getEntityData().set(PITCH_TARGET, pitch);
    }

    public double getTargetPitch() {
        return this.getEntityData().get(PITCH_TARGET);
    }

    /**
     * Set the target pitch for the squid to rotate to. This can be outside the normal rotation range of
     * [-Math.PI, Math.PI], so that a squid can rotate "the short way" from 170 degrees to 190 degrees instead of
     * "the long way" of 170 degrees to -170 degrees.
     */
    public void setTargetPitch(double p) {
        double currentPitchClamped = RotationHelper.resetRotationValueWithinRange(this.getEntityData().get(PITCH));
        double difference = RotationHelper.resetRotationValueWithinRange(p) - currentPitchClamped;
        // If entering either if block, would be turning the long way. Turn the short way instead
        if (difference > Math.PI) {
            difference -= DOUBLE_PI;
        }
        else if (difference < -Math.PI) {
            difference += DOUBLE_PI;
        }
        this.getEntityData().set(PITCH_PREV, currentPitchClamped);
        this.getEntityData().set(PITCH, currentPitchClamped);
        this.getEntityData().set(PITCH_TARGET, currentPitchClamped + difference);
    }

    public double getPreviousYaw() {
        return this.getEntityData().get(YAW_PREV);
    }

    public double getYaw() {
        return this.getEntityData().get(YAW);
    }

    private void setYaw(double yaw) {
        this.getEntityData().set(YAW_PREV, this.getEntityData().get(YAW));
        this.getEntityData().set(YAW, yaw);
    }

    public void forceYawInstant(double yaw) {
        yaw = RotationHelper.resetRotationValueWithinRange(yaw);
        this.getEntityData().set(YAW_PREV, yaw);
        this.getEntityData().set(YAW, yaw);
        this.getEntityData().set(YAW_TARGET, yaw);
    }

    public double getTargetYaw() {
        return this.getEntityData().get(YAW_TARGET);
    }

    /**
     * Set the target yaw for the squid to rotate to. This can be outside the normal rotation range of
     * [-Math.PI, Math.PI], so that a squid can rotate "the short way" from 170 degrees to 190 degrees instead of
     * "the long way" of 170 degrees to -170 degrees.
     */
    public void setTargetYaw(double y) {
        double currentYawClamped = RotationHelper.resetRotationValueWithinRange(this.getEntityData().get(YAW));
        double difference = RotationHelper.resetRotationValueWithinRange(y) - currentYawClamped;
        // If entering either if block, would be turning the long way. Turn the short way instead
        if (difference > Math.PI) {
            difference -= DOUBLE_PI;
        }
        else if (difference < -Math.PI) {
            difference += DOUBLE_PI;
        }
        this.getEntityData().set(YAW_PREV, currentYawClamped);
        this.getEntityData().set(YAW, currentYawClamped);
        this.getEntityData().set(YAW_TARGET, currentYawClamped + difference);
    }

    public double getPreviousRoll() {
        return this.getEntityData().get(ROLL_PREV);
    }

    public double getRoll() {
        return this.getEntityData().get(ROLL);
    }

    private void setRoll(double roll) {
        this.getEntityData().set(ROLL_PREV, this.getEntityData().get(ROLL));
        this.getEntityData().set(ROLL, roll);
    }

    public void forceRollInstant(double roll) {
        roll = RotationHelper.resetRotationValueWithinRange(roll);
        this.getEntityData().set(ROLL_PREV, roll);
        this.getEntityData().set(ROLL, roll);
        this.getEntityData().set(ROLL_TARGET, roll);
    }

    public double getTargetRoll() {
        return this.getEntityData().get(ROLL_TARGET);
    }

    /**
     * Set the target roll for the squid to rotate to. This can be outside the normal rotation range of
     * [-Math.PI, Math.PI], so that a squid can rotate "the short way" from 170 degrees to 190 degrees instead of
     * "the long way" of 170 degrees to -170 degrees.
     */
    public void setTargetRoll(double r) {
        double currentRollClamped = RotationHelper.resetRotationValueWithinRange(this.getEntityData().get(ROLL));
        double difference = RotationHelper.resetRotationValueWithinRange(r) - currentRollClamped;
        // If entering either if block, would be turning the long way. Turn the short way instead
        if (difference > Math.PI) {
            difference -= DOUBLE_PI;
        }
        else if (difference < -Math.PI) {
            difference += DOUBLE_PI;
        }
        this.getEntityData().set(ROLL_PREV, currentRollClamped);
        this.getEntityData().set(ROLL, currentRollClamped);
        this.getEntityData().set(ROLL_TARGET, currentRollClamped + difference);
    }

    public boolean getShaking() {
        return this.getEntityData().get(SHAKING);
    }

    public void setShaking(boolean shaking) {
        this.getEntityData().set(SHAKING, shaking);
    }

    public byte getCountdownTicks() {
        return this.getEntityData().get(COUNTDOWN_TICKS);
    }

    public void setCountdownTicks(byte countdownTicks) {
        this.getEntityData().set(COUNTDOWN_TICKS, countdownTicks);
    }

    public void beginCountdown() {
        SynchedEntityData sed = this.getEntityData();
        sed.set(SHAKING, true);
        sed.set(COUNTDOWN_TICKS, DataReference.DEFAULT_COUNTDOWN_LENGTH);
    }

    public boolean getBlasting() {
        return this.getEntityData().get(BLAST_TICKS_REMAINING) > 0;
    }

    public byte getBlastTicksRemaining() {
        return this.getEntityData().get(BLAST_TICKS_REMAINING);
    }

    public void setBlastTicksRemaining(byte ticksRemaining) {
        this.getEntityData().set(BLAST_TICKS_REMAINING, ticksRemaining);
    }

    public boolean getTricking() {
        return this.getEntityData().get(TRICK_TICKS_REMAINING) > 0;
    }

    public byte getTrickTicksRemaining() {
        return this.getEntityData().get(TRICK_TICKS_REMAINING);
    }

    public void setTrickTicksRemaining(byte ticksRemaining) {
        this.getEntityData().set(TRICK_TICKS_REMAINING, ticksRemaining);
    }

    public void doTrick(TrickParameters trickParams) {
        this.trickParams = trickParams;
        this.getEntityData().set(TRICK_TICKS_REMAINING, DataReference.DEFAULT_TRICK_LENGTH);
    }

    public void blastoff() {
        SynchedEntityData sed = this.getEntityData();
        sed.set(SHAKING, true);
        sed.set(BLAST_TICKS_REMAINING, DataReference.DEFAULT_BLAST_LENGTH);
        this.playSound(RSSounds.BLASTOFF.get(), 1.0F, 1.0F);
        this.addForce(2.952);
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

    public int getTargetNote(int index) {
        return this.targetNotes[index];
    }

    // SECTION FOR BREEDING
    /**
     * Intention is for babies to turn into adults in 5 minutes real-time
     */
    @Override
    protected int getBabyStartAge() {
        return -6000;
    }

    public boolean canBreed() {
        return !this.isBaby() && this.getAge() == 0 && !this.hasPassengers();
    }

    @Override
    public @org.jspecify.annotations.Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return RSEntityTypes.SQUID_TYPE.get().create(level, EntitySpawnReason.BREEDING);
    }

    /**
     * Tells the client to spawn heart particles above the head of the rocket squids, used for when they "breed".
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
     * Run when adult rocket squids try to "breed".
     *
     * @param partner The potential partner to "breed" with.
     */
    private void breed(RocketSquidEntity partner) {
        if (!this.level().isClientSide()) {
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
                    this.spawnHearts((ServerLevel) this.level());
                    break;
                default:
                    // Probably impossible...
                    this.forcedBlast = true;
                    partner.forcedBlast = true;
            }
        }
    }

    public void spawnChildFromBreeding(ServerLevel level, RocketSquidEntity partner) {
        AgeableMob offspring = this.getBreedOffspring(level, partner);
        final net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent event = new net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent(this, partner, offspring);
        final boolean cancelled = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled();
        offspring = event.getChild();
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.setAge(CommonConfig.BREED_COOLDOWN);
            partner.setAge(CommonConfig.BREED_COOLDOWN);
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
        this.setAge(CommonConfig.BREED_COOLDOWN);
        partner.setAge(CommonConfig.BREED_COOLDOWN);
        level.broadcastEntityEvent(this, (byte) 18);
    }

    // SECTION FOR RIDING
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
            double riderDownness = new Vec3(0.0, -1.0, 0.0).dot(RotationHelper.applySquidRotationFull(this, new Vec3(0.0, 0.0, -1.0)));
            if (riderDownness < -0.17) {
                // Angle between rider and ground over ~100 degrees, so the squid hits first and takes the damage
                return false;
            }
            else if (riderDownness < 0.0) {
                // Just over 90 degrees so the squid takes half the damage
                damageMultiplier *= 0.5;
            }
            return super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    @Override
    protected void removePassenger(@NotNull Entity passenger) {
        super.removePassenger(passenger);
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        } else if (this.getBlasting()) {
            this.riderToThrow = passenger;
        }
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn) {
        return !this.isBaby();
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

    // SECTION FOR CLIENT EVENTS
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
            double pitchForTranslation = exactPitchRads - (Math.PI / 2.0);

            double prevYawRads = data.get(YAW_PREV);
            double yawRads = data.get(YAW);
            double exactYawRads = prevYawRads + (yawRads - prevYawRads) * partialTick;

            double prevRollRads = data.get(ROLL_PREV);
            double rollRads = data.get(ROLL);
            double exactRollRads = prevRollRads + (rollRads - prevRollRads) * partialTick;

            double pitchTranslation = -0.2 * Math.abs(Math.sin(pitchForTranslation / 2.0));
            double rollTranslation = -0.2 * Math.abs(Math.sin(exactRollRads / 2.0));

            this.riderRotated = true;
            PoseStack stack = event.getPoseStack();
            stack.pushPose();
            // Rotate the rider to match the squid's rotation
            Quaternionf quat = new Quaternionf()
                    .rotateLocalZ((float) exactRollRads)
                    .rotateLocalX((float) pitchForTranslation)
                    .rotateLocalY((float) -exactYawRads);
            stack.mulPose(quat);
            // Keep the rider from floating away from the saddle
            stack.translate(0.0, pitchTranslation + rollTranslation, 0.0);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void popRotation(RenderPlayerEvent.Post event) {
        if (this.riderRotated) {
            event.getPoseStack().popPose();
            this.riderRotated = false;
        }
    }

    // SECTION FOR PAIN AND DEATH
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

    @Override
    public boolean shouldDropExperience() {
        return !this.isBaby();
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        }
        super.remove(reason);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return null;
    }

    /**
     * Returns the sound this mob makes when it dies.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
}
