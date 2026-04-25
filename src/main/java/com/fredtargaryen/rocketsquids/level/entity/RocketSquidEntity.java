// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity;

import com.fredtargaryen.rocketsquids.RSEntityTypes;
import com.fredtargaryen.rocketsquids.RSItems;
import com.fredtargaryen.rocketsquids.RSSounds;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.config.CommonConfig;
import com.fredtargaryen.rocketsquids.level.attachment.RocketSquidData;
import com.fredtargaryen.rocketsquids.level.datacomponent.SqueleporterData;
import com.fredtargaryen.rocketsquids.level.entity.ai.AdultFlopAroundGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.AdultSwimAroundGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.BlastoffGoal;
import com.fredtargaryen.rocketsquids.level.entity.ai.ShakeGoal;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.AdultCapDataMessage;
import com.fredtargaryen.rocketsquids.network.message.SquidFireworkMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

import static com.fredtargaryen.rocketsquids.RSAttachmentTypes.SQUID;
import static com.fredtargaryen.rocketsquids.RSDataComponentTypes.SQUELEPORTER;

public class RocketSquidEntity extends AbstractRocketSquidEntity {
    protected int breedCooldown;
    protected boolean breedable;

    /// //////////////
    /// Client only///
    /// //////////////
    public boolean riderRotated;

    //May have to remove and use capability instead
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(RocketSquidEntity.class, EntityDataSerializers.BOOLEAN);

    public RocketSquidEntity(Level par1World) {
        super(RSEntityTypes.SQUID_TYPE.get(), par1World);
        this.breedable = true;
        this.riderRotated = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SADDLED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlastoffGoal(this));
        this.goalSelector.addGoal(1, new ShakeGoal(this));
        this.goalSelector.addGoal(2, new AdultSwimAroundGoal(this));
        this.goalSelector.addGoal(3, new AdultFlopAroundGoal(this));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        RocketSquidData data = this.getData(SQUID);
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
            data.setForcedBlast(true);
            this.newPacketRequired = true;
        }
        if (onFire || data.getForcedBlast()) {
            this.playSound(RSSounds.BLASTOFF.get(), 0.5F, 1.0F);
            data.setBlasting(true);
        }

        //Rotate towards target pitch
        double trp = data.getTargetRotPitch();
        double rp = data.getRotPitch();
        if (trp != rp) {
            //Squids rotate <= 180 degrees either way.
            //The squid can rotate out of the interval [-PI, PI].
            rp += (trp - rp) * rotateSpeed;
            data.setRotPitch(rp);
            this.newPacketRequired = true;
        }

        //Rotate towards target yaw
        double trY = data.getTargetRotYaw();
        double ry = data.getRotYaw();
        if (trY != ry) {
            ry += (trY - ry) * rotateSpeed;
            data.setRotYaw(ry);
            this.newPacketRequired = true;
        }

        Vec3 pos = this.position();

        if (this.level().isClientSide()) {
            //Client side
            //Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if (data.getBlasting()) {
                //Tentacles quickly close up
                this.tentacleAngle = 0;
            } else if (data.getShaking()) {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            } else {
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.isInWater() ? (float) ((Math.PI / 6) + (Mth.sin((float) Math.toRadians(4 * (this.tickCount % 360))) * Math.PI / 6)) : 0;
            }
            if (data.getBlasting()) {
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
            if (this.isInWater() && !this.getShaking() && !this.getBlasting()) {
                this.moveToWherePointing();
            }
            if (this.newPacketRequired) {
                PacketDistributor.sendToPlayersNear((ServerLevel) this.level(), null, pos.x, pos.y, pos.z, 64, new AdultCapDataMessage(this.getUUID(), data.serializeNBT(null)));
                this.newPacketRequired = false;
            }
        }
    }

    /**
     * Moves the entity based on strafe, forward (?) and something else
     */
    @Override
    public void travel(@NotNull Vec3 motion) {
        this.move(MoverType.SELF, this.getDeltaMovement());
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
                    SqueleporterData data = newStack.get(SQUELEPORTER);
                    Vec3 pos = player.position();
                    player.level().playSound(null, pos.x, pos.y, pos.z, RSSounds.SQUIDTP_IN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    // Set squid data
                    CompoundTag entityData = new CompoundTag();
                    this.addAdditionalSaveData(entityData);
                    CompoundTag attachmentData = this.getData(SQUID).serializeNBT(null);
                    newStack.set(SQUELEPORTER, new SqueleporterData(entityData, attachmentData));
                    this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
                    EquipmentSlot handEquip = EquipmentSlot.MAINHAND;
                    if (hand == InteractionHand.OFF_HAND) {
                        handEquip = EquipmentSlot.OFFHAND;
                    }
                    player.setItemSlot(handEquip, newStack);
                    player.getCooldowns().addCooldown(newStack.getItem(), 10);
                } else if (interactItem == Items.FLINT_AND_STEEL) {
                    // if the player isn't in creative we damage the flint and steel
                    if (!player.isCreative()) {
                        interactStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }
                    this.getData(SQUID).setForcedBlast(true);
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.SADDLE) {
                    if (!this.getSaddled()) {
                        interactStack.consume(1, player);
                        this.setSaddled(true);
                    }
                    player.startRiding(this);
                    return InteractionResult.SUCCESS;
                } else if (interactItem == Items.FEATHER) {
                    this.setShaking(true);
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
        if (this.getBlasting()) {
            Entity passenger = this.getFirstPassenger();
            if (passenger != null) {
                Vec3 motion = passenger.getDeltaMovement();
                passenger.setDeltaMovement(motion.x * 2.5, motion.y * 2.5, motion.z * 2.5);
            }
        }
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        }
        super.remove(reason);
    }

    /**
     * Spawns the squid firework particles for when they explode.
     */
    @OnlyIn(Dist.CLIENT)
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
        return this.breedable && this.breedCooldown <= 0 && !this.hasPassengers();
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
                    this.breedCooldown = CommonConfig.BREED_COOLDOWN;
                    BabyRocketSquidEntity baby = RSEntityTypes.BABY_SQUID_TYPE.get().create(this.level());
                    assert baby != null;
                    baby.moveTo(thisPos.x, thisPos.y, thisPos.z, 0.0F, 0.0F);
                    this.level().addFreshEntity(baby);
                    this.spawnHearts((ServerLevel) this.level());
                    break;
                case -1:
                    // the one with the lesser UUID doesn't
                    this.breedCooldown = CommonConfig.BREED_COOLDOWN;
                    this.spawnHearts((ServerLevel) this.level());
                    break;
                default:
                    // Probably impossible...
                    this.getData(SQUID).setForcedBlast(true);
                    partner.getData(SQUID).setForcedBlast(true);
            }
        }
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    protected void tickLeash() {
        super.tickLeash();
        if (this.isLeashed()) {
            Entity holder = this.getLeashHolder();
            if (holder != null && holder.level() == this.level()) {
                float f = this.distanceTo(holder);

                if (f > 8.0F) {
                    this.setDeltaMovement(0.0F, 0.0F, 0.0F);
                }

                if (f > 6.0F) {
                    Vec3 thisPos = this.position();
                    Vec3 holderPos = holder.position();
                    double d0 = (holderPos.x - thisPos.x) / (double) f;
                    double d1 = (holderPos.y - thisPos.y) / (double) f;
                    double d2 = (holderPos.z - thisPos.z) / (double) f;
                    Vec3 motion = this.getDeltaMovement();
                    this.setDeltaMovement(motion.x + d0 * Math.abs(d0) * 0.4D,
                            motion.y + d1 * Math.abs(d1) * 0.4D,
                            motion.z + d2 * Math.abs(d2) * 0.4D);
                }
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
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource damageSource) {
        if (this.isVehicle()) {
            if (Math.sin(this.getData(SQUID).getRotPitch()) < -0.7071067811865) {
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
        if (this.getBlasting()) {
            Vec3 passengerMotion = passenger.getDeltaMovement();
            passenger.setDeltaMovement(passengerMotion.x * 2.5,
                    passengerMotion.y * 2.5,
                    passengerMotion.z * 2.5);
        }
        if (this.level().isClientSide()) {
            NeoForge.EVENT_BUS.unregister(this);
        }
    }

    @Override
    public boolean startRiding(@NotNull Entity entityIn, boolean force) {
        return false;
    }

    @Override
    protected boolean canRide(@NotNull Entity entityIn) {
        return this.isVehicle();
    }

    public boolean getSaddled() {
        return this.entityData.get(SADDLED);
    }

    /**
     * Set or remove the saddle of the squid.
     */
    private void setSaddled(boolean saddled) {
        if (saddled) {
            this.entityData.set(SADDLED, Boolean.TRUE);
        } else {
            this.entityData.set(SADDLED, Boolean.FALSE);
        }
    }

    /**
     * dropEquipment
     */
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.getSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
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

    /**
     * If the rider should be dismounted from the entity when the entity goes under water
     *
     * @param rider The entity that is riding
     * @return if the entity should be dismounted when under water
     */
    @Override
    public boolean canBeRiddenUnderFluidType(Entity rider) {
        return true;
    }

    /////////////////
    //CLIENT EVENTS//
    /////////////////

    /**
     * Add transformations to put player on back of squid.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void addRotation(RenderPlayerEvent.Pre event) {
        if (event.getEntity() == this.getFirstPassenger()) {
            float partialTick = event.getPartialTick();

            RocketSquidData data = this.getData(SQUID);
            double prevPitchRads = data.getPrevRotPitch();
            double pitchRads = data.getRotPitch();
            double exactPitchRads = prevPitchRads + (pitchRads - prevPitchRads) * partialTick;
            double squidAngle = exactPitchRads - (Math.PI / 2.0);

            double prevYawRads = data.getPrevRotYaw();
            double yawRads = data.getRotYaw();
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

    @OnlyIn(Dist.CLIENT)
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
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("id", EntityType.getKey(RSEntityTypes.SQUID_TYPE.get()).toString());
        Vec3 motion = this.getDeltaMovement();
        compound.putDouble("force", Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z));
        compound.putBoolean("Saddle", this.getSaddled());
        compound.putInt("Breed Cooldown", this.breedCooldown);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        //Force comes from reading motion tags
        this.setSaddled(compound.getBoolean("Saddle"));
        this.breedCooldown = compound.getShort("Breed Cooldown");
    }

    //////////////////////
    //CAPABILITY METHODS//

    /// ///////////////////
    public double getPrevRotPitch() {
        return this.getData(SQUID).getPrevRotPitch();
    }

    public double getPrevRotYaw() {
        return this.getData(SQUID).getPrevRotYaw();
    }

    public double getRotPitch() {
        return this.getData(SQUID).getRotPitch();
    }

    public double getRotYaw() {
        return this.getData(SQUID).getRotYaw();
    }

    public void forceRotPitch(double rotPitch) {
        this.getData(SQUID).setRotPitch(rotPitch);
        this.getData(SQUID).setTargetRotPitch(rotPitch);
    }

    public void forceRotYaw(double rotYaw) {
        this.getData(SQUID).setRotYaw(rotYaw);
        this.getData(SQUID).setTargetRotYaw(rotYaw);
    }

    public void setTargetRotPitch(double targPitch) {
        if (targPitch != this.getData(SQUID).getTargetRotPitch()) {
            this.getData(SQUID).setTargetRotPitch(targPitch);
            this.newPacketRequired = true;
        }
    }

    public void setTargetRotYaw(double targYaw) {
        if (targYaw != this.getData(SQUID).getTargetRotYaw()) {
            this.getData(SQUID).setTargetRotYaw(targYaw);
            this.newPacketRequired = true;
        }
    }

    public double getTargRotPitch() {
        return this.getData(SQUID).getTargetRotPitch();
    }

    public double getTargRotYaw() {
        return this.getData(SQUID).getTargetRotYaw();
    }

    public boolean getBlasting() {
        return this.getData(SQUID).getBlasting();
    }

    public void setBlasting(boolean b) {
        if (b != this.getData(SQUID).getBlasting()) {
            this.getData(SQUID).setBlasting(b);
            this.newPacketRequired = true;
        }
    }

    public boolean getShaking() {
        return this.getData(SQUID).getShaking();
    }

    public void setShaking(boolean b) {
        if (b != this.getData(SQUID).getShaking()) {
            this.getData(SQUID).setShaking(b);
            this.newPacketRequired = true;
        }
    }

    public int getShakeTicks() {
        return this.getData(SQUID).getShakeTicks();
    }

    public void setShakeTicks(int ticks) {
        this.getData(SQUID).setShakeTicks(ticks);
    }

    public boolean getBlastToStatue() {
        return this.getData(SQUID).getBlastToStatue();
    }

    public void setBlastToStatue(boolean blast) {
        this.getData(SQUID).setBlastToStatue(blast);
    }

    public boolean getForcedBlast() {
        return this.getData(SQUID).getForcedBlast();
    }

    public byte getTargetNote(byte index) {
        return this.getData(SQUID).getTargetNotes()[index];
    }
}
