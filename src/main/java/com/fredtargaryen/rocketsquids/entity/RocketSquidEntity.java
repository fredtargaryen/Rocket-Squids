package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.client.particle.SquidFireworkParticle;
import com.fredtargaryen.rocketsquids.entity.ai.AdultFlopAroundGoal;
import com.fredtargaryen.rocketsquids.entity.ai.AdultSwimAroundGoal;
import com.fredtargaryen.rocketsquids.entity.ai.BlastoffGoal;
import com.fredtargaryen.rocketsquids.entity.ai.ShakeGoal;
import com.fredtargaryen.rocketsquids.entity.capability.adult.IAdultCapability;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageAdultCapData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class RocketSquidEntity extends AbstractSquidEntity {
    private IAdultCapability squidCap;

    ///////////////
    //Client only//
    ///////////////
    public boolean riderRotated;

    protected short breedCooldown;

    //May have to remove and use capability instead
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(RocketSquidEntity.class, DataSerializers.BOOLEAN);

    public RocketSquidEntity(World par1World) {
        super(RocketSquidsBase.SQUID_TYPE, par1World);
        this.getCapability(RocketSquidsBase.ADULTCAP).ifPresent(cap -> RocketSquidEntity.this.squidCap = cap);
        this.riderRotated = false;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData livingEntityData, @Nullable CompoundNBT itemNbt) {
        ILivingEntityData ield = super.onInitialSpawn(world, difficulty, reason, livingEntityData, itemNbt);
        return ield;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SADDLED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlastoffGoal(this));
        this.goalSelector.addGoal(1, new ShakeGoal(this));
        this.goalSelector.addGoal(2, new AdultSwimAroundGoal(this));
        this.goalSelector.addGoal(3, new AdultFlopAroundGoal(this));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
	@Override
	public void livingTick() {
        super.livingTick();

        //Do on client and server
        //Fraction of distance to target rotation to rotate by each server tick
        double rotateSpeed;
        if(this.inWater) {
            Vec3d motion = this.getMotion();
            this.setMotion(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9);
            rotateSpeed = 0.06;
        }
        else {
            Vec3d oldMotion = this.getMotion();
            double motionX = oldMotion.x;
            double motionY = oldMotion.y;
            double motionZ = oldMotion.z;
            if(this.recentlyHit > 0) {
                motionX = 0.0D;
                motionZ = 0.0D;
            }
            if (this.isPotionActive(Effects.LEVITATION)) {
                motionY += 0.05D * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1) - motionY; //levitation
            }
            else if (!this.hasNoGravity()) {
                motionY -= 0.08D;
            }
            motionX *= 0.9800000190734863D;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.9800000190734863D;
            rotateSpeed = 0.15;
            this.setMotion(motionX, motionY, motionZ);
        }

        boolean onFire = false;
        if(this.isBurning() || this.isInLava()) {
            onFire = true;
            this.squidCap.setForcedBlast(true);
            this.newPacketRequired = true;
        }
        if(onFire || this.squidCap.getForcedBlast()) {
            this.playSound(Sounds.BLASTOFF, 0.5F, 1.0F);
            this.squidCap.setBlasting(true);
        }

        //Rotate towards target pitch
        double trp = this.squidCap.getTargetRotPitch();
        double rp = this.squidCap.getRotPitch();
        if(trp != rp) {
            //Squids rotate <= 180 degrees either way.
            //The squid can rotate out of the interval [-PI, PI].
            rp += (trp - rp) * rotateSpeed;
            this.squidCap.setRotPitch(rp);
            this.newPacketRequired = true;
        }

        //Rotate towards target yaw
        double trY = this.squidCap.getTargetRotYaw();
        double ry = this.squidCap.getRotYaw();
        if(trY != ry) {
            ry += (trY - ry) * rotateSpeed;
            this.squidCap.setRotYaw(ry);
            this.newPacketRequired = true;
        }

        Vec3d pos = this.getPositionVec();

        if(this.world.isRemote) {
            //Client side
            //Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if(this.squidCap.getShaking()) {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            }
            else if(this.squidCap.getBlasting()) {
                //Tentacles quickly close up
                this.tentacleAngle = 0;
            }
            else {
                //If in water, tentacles oscillate normally
                this.tentacleAngle = this.inWater ? (float) ((Math.PI / 6) + (MathHelper.sin((float) Math.toRadians(4 * (this.ticksExisted % 360))) * Math.PI / 6)) : 0;
            }
            if(this.squidCap.getBlasting()) {
                if(this.inWater) {
                    double smallerX = pos.x - 0.25;
                    double largerX = pos.x + 0.25;
                    double smallerZ = pos.z - 0.25;
                    double largerZ = pos.z + 0.25;
                    this.world.addParticle(ParticleTypes.BUBBLE, smallerX, pos.y, smallerZ, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.BUBBLE, smallerX, pos.y, largerZ, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.BUBBLE, largerX, pos.y, smallerZ, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.BUBBLE, largerX, pos.y, largerZ, 0.0, 0.0, 0.0);
                }
                else
                {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                }
            }
        }
        else {
            //Server side
            //Handle breeding ticks
            if(this.breedCooldown > 0) {
                --this.breedCooldown;
            }
            if(this.isInWater() && !this.getShaking() && !this.getBlasting()) {
                this.moveToWherePointing();
            }
            if(this.newPacketRequired) {
                MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64, this.dimension)), new MessageAdultCapData(this.getUniqueID(), this.squidCap));
                this.newPacketRequired = false;
            }
        }
    }

    /**
     * Moves the entity based on strafe, forward (?) and something else
     */
    @Override
    public void travel(Vec3d motion) {
        this.move(MoverType.SELF, this.getMotion());
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if(!this.world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack == ItemStack.EMPTY) {
                if (this.getSaddled() && !this.isBeingRidden()) {
                    player.startRiding(this);
                    return true;
                }
            }
            else {
                Item i = stack.getItem();
                if(i == RocketSquidsBase.SQUELEPORTER_INACTIVE) {
                    //The squeleporter is inactive so store the squid here
                    ItemStack newStack = RocketSquidsBase.SQUELEPORTER_ACTIVE.getDefaultInstance();
                    newStack.getCapability(RocketSquidsBase.SQUELEPORTER_CAP).ifPresent(cap -> {
                        Vec3d pos = player.getPositionVec();
                        player.world.playSound(null, pos.x, pos.y, pos.z, Sounds.SQUIDTP_IN, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        cap.setSquid(this);
                        this.remove();
                    });
                    player.setHeldItem(hand, newStack);
                    player.getCooldownTracker().setCooldown(newStack.getItem(), 10);
                }
                else if (i == Items.FLINT_AND_STEEL) {
                    stack.attemptDamageItem(1, player.getRNG(), (ServerPlayerEntity) player);
                    this.squidCap.setForcedBlast(true);
                    return true;
                }
                else if (i == Items.SADDLE) {
                    if (!this.getSaddled()) {
                        stack.attemptDamageItem(1, player.getRNG(), (ServerPlayerEntity) player);
                        this.setSaddled(true);
                    }
                    player.startRiding(this);
                    return true;
                }
                else if (i == Items.FEATHER) {
                    this.setShaking(true);
                    return true;
                }
                else {
                    if (this.getSaddled() && !this.isBeingRidden()) {
                        player.startRiding(this);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    public void explode() {
        if(!this.world.isRemote) {
            Vec3d pos = this.getPositionVec();
            this.world.createExplosion(this, pos.x, pos.y, pos.z, 3.0F, Explosion.Mode.DESTROY);
            int noSacs = 3 + this.rand.nextInt(3);
            int noTubes = 2 + this.rand.nextInt(3);
            for (int x = 0; x < noSacs; ++x) {
                ItemEntity entityitem = new ItemEntity(this.world, pos.x, pos.y, pos.z, new ItemStack(RocketSquidsBase.NITRO_SAC));
                double motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                double motionY = -0.2;
                double motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                entityitem.setMotion(motionX, motionY, motionZ);
                this.world.addEntity(entityitem);
            }
            for (int x = 0; x < noTubes; ++x) {
                ItemEntity entityitem = new ItemEntity(this.world, pos.x, pos.y, pos.z, new ItemStack(RocketSquidsBase.TURBO_TUBE));
                double motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                double motionY = -0.2;
                double motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                entityitem.setMotion(motionX, motionY, motionZ);
                this.world.addEntity(entityitem);
            }
        }
        this.remove();
    }

    @Override
    public void remove() {
        if(this.world.isRemote && this.squidCap.getForcedBlast()) {
            this.doFireworkParticles();
        }
        if(this.getBlasting()) {
            Entity passenger = this.getControllingPassenger();
            if(passenger != null) {
                Vec3d motion = passenger.getMotion();
                passenger.setMotion(motion.x * 2.5, motion.y * 2.5, motion.z * 2.5);
            }
        }
        if(this.world.isRemote) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        super.remove();
    }

    @OnlyIn(Dist.CLIENT)
    private void doFireworkParticles() {
        ParticleManager effectRenderer = Minecraft.getInstance().particles;
        Vec3d pos = this.getPositionVec();
        effectRenderer.addEffect(new SquidFireworkParticle.SquidStarter(this.world, pos.x, pos.y, pos.z, effectRenderer));
    }

    /**
     * Applies a velocity to the entities (unless they're riding), to push them away from each other.
     */
    public void applyEntityCollision(Entity obstacle) {
        Entity passenger = this.getControllingPassenger();
        if(passenger == null || passenger != obstacle) {
            //Obstacle is not the rider, so apply collision
            if (!obstacle.noClip && !this.noClip) {
                Vec3d thisPos = this.getPositionVec();
                if(!this.world.isRemote && obstacle.getType() == RocketSquidsBase.SQUID_TYPE && this.breedCooldown == 0) {
                    this.breedCooldown = 3600;
                    BabyRocketSquidEntity baby = new BabyRocketSquidEntity(this.world);
                    baby.setLocationAndAngles(thisPos.x, thisPos.y, thisPos.z, 0.0F, 0.0F);
                    this.world.addEntity(baby);
                }
                Vec3d obstaclePos = obstacle.getPositionVec();
                double xDist = obstaclePos.x - thisPos.x;
                double zDist = obstaclePos.z - thisPos.z;
                double yDist = obstaclePos.y - thisPos.y;
                double largerDist = MathHelper.absMax(xDist, MathHelper.absMax(yDist, zDist));

                if (largerDist >= 0.009999999776482582D) {
                    largerDist = (double) MathHelper.sqrt(largerDist);
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
                    xDist *= 0.05000000074505806D;
                    yDist *= 0.05000000074505806D;
                    zDist *= 0.05000000074505806D;

                    this.addVelocity(-xDist * 0.02, -yDist * 0.02, -zDist * 0.02);
                    obstacle.addVelocity(xDist * 0.98, yDist * 0.98, zDist * 0.98);
                }
            }
        }
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    protected void updateLeashedState() {
        super.updateLeashedState();
        if(this.getLeashed()) {
            Entity holder = this.getLeashHolder();
            if (holder != null && holder.world == this.world) {
                float f = this.getDistance(holder);

                if (f > 8.0F) {
                    this.setMotion(0.0F, 0.0F, 0.0F);
                }

                if (f > 6.0F) {
                    Vec3d thisPos = this.getPositionVec();
                    Vec3d holderPos = holder.getPositionVec();
                    double d0 = (holderPos.x - thisPos.x) / (double) f;
                    double d1 = (holderPos.y - thisPos.y) / (double) f;
                    double d2 = (holderPos.z - thisPos.z) / (double) f;
                    Vec3d motion = this.getMotion();
                    this.setMotion(motion.x + d0 * Math.abs(d0) * 0.4D,
                            motion.y + d1 * Math.abs(d1) * 0.4D,
                            motion.z + d2 * Math.abs(d2) * 0.4D);
                }
            }
        }
    }

    //////////////////
    //RIDING METHODS//
    //////////////////

    @Override
    protected void addPassenger(Entity p) {
        if(this.getPassengers().size() == 0) {
            super.addPassenger(p);
            if(this.world.isRemote) {
                MinecraftForge.EVENT_BUS.register(this);
            }
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        List passengers = this.getPassengers();
        if(passengers.isEmpty()) {
            return null;
        }
        else {
            return this.getPassengers().get(0);
        }
    }

    public boolean hasVIPRider() {
        Entity passenger = this.getControllingPassenger();
        if (passenger instanceof PlayerEntity) {
            return ((PlayerEntity) passenger).getHeldItem(Hand.MAIN_HAND)
                        .getItem() == RocketSquidsBase.SQUAVIGATOR
                    || ((PlayerEntity) passenger)
                        .getHeldItem(Hand.OFF_HAND).getItem() == RocketSquidsBase.SQUAVIGATOR;
        }
        return false;
    }

    /**
     * Checks if squid pitch is less than -45 degrees and more than -135 degrees.
     * Between these angles the player would appear to hit the ground first so the player should be hurt.
     */
    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        if (this.isBeingRidden()) {
            if(Math.sin(this.squidCap.getRotPitch()) < -0.7071067811865) {
                for(Entity entity : this.getPassengers()) {
                    entity.onLivingFall(distance, damageMultiplier);
                }
            }
        }
        return false;
    }

    /**
     * Should keep the passenger on, or at least around, the squid's back.
     */
    @Override
    public void updatePassenger(Entity passenger) {
        if(this.isPassenger(passenger)) {
            Vec3d pos = this.getPositionVec();
            passenger.setPosition(pos.x, pos.y + 0.355, pos.z);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (this.getBlasting()) {
            Vec3d passengerMotion = passenger.getMotion();
            passenger.setMotion(passengerMotion.x * 2.5,
                    passengerMotion.y * 2.5,
                    passengerMotion.z * 2.5);
        }
        if(this.world.isRemote) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        return false;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return this.getPassengers().size() == 0;
    }

    public boolean getSaddled() {
        return ((Boolean)this.dataManager.get(SADDLED)).booleanValue();
    }

    /**
     * Set or remove the saddle of the squid.
     */
    private void setSaddled(boolean saddled) {
        if (saddled) {
            this.dataManager.set(SADDLED, Boolean.valueOf(true));
        }
        else {
            this.dataManager.set(SADDLED, Boolean.valueOf(false));
        }
    }

    /**
     * dropEquipment
     */
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.getSaddled()) {
            this.entityDropItem(Items.SADDLE);
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
    public boolean canBeRiddenInWater(Entity rider) {
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
        if(this.isPassenger(event.getPlayer())) {
            double prevPitch_r = this.squidCap.getPrevRotPitch();
            double pitch_r = this.squidCap.getRotPitch();
			float partialTick = event.getPartialRenderTick();
            double exactPitch_r = prevPitch_r + (pitch_r - prevPitch_r) * partialTick;
			double yaw_r = this.squidCap.getRotYaw();
            this.riderRotated = true;
            MatrixStack stack = event.getMatrixStack();
            stack.push();
            Quaternion quat = Vector3f.YP.rotation((float) -yaw_r);
            quat.multiply(Vector3f.XP.rotation((float) (exactPitch_r - (Math.PI / 2))));
            stack.rotate(quat);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void popRotation(RenderPlayerEvent.Post event) {
        if(this.riderRotated) {
            event.getMatrixStack().pop();
            this.riderRotated = false;
        }
    }

    ///////
    //NBT//
    ///////
    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("id", EntityType.getKey(RocketSquidsBase.SQUID_TYPE).toString());
        Vec3d motion = this.getMotion();
        compound.putDouble("force", Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z));
        compound.putBoolean("Saddle", this.getSaddled());
        compound.putShort("Breed Cooldown", this.breedCooldown);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        //Force comes from reading motion tags
        this.setSaddled(compound.getBoolean("Saddle"));
        this.breedCooldown = compound.getShort("Breed Cooldown");
    }

    //////////////////////
    //CAPABILITY METHODS//
    //////////////////////
    public double getPrevRotPitch() {
        return this.squidCap.getPrevRotPitch();
    }

    public double getPrevRotYaw() {
        return this.squidCap.getPrevRotYaw();
    }

    public double getRotPitch() {
        return this.squidCap.getRotPitch();
    }

    public double getRotYaw() {
        return this.squidCap.getRotYaw();
    }

    public void forceRotPitch(double rotPitch) {
        this.squidCap.setRotPitch(rotPitch);
        this.squidCap.setTargetRotPitch(rotPitch);
    }

    public void forceRotYaw(double rotYaw) {
        this.squidCap.setRotYaw(rotYaw);
        this.squidCap.setTargetRotYaw(rotYaw);
    }

    public void setTargetRotPitch(double targPitch) {
        if(targPitch != this.squidCap.getTargetRotPitch()) {
            this.squidCap.setTargetRotPitch(targPitch);
            this.newPacketRequired = true;
        }
    }

    public void setTargetRotYaw(double targYaw) {
        if (targYaw != this.squidCap.getTargetRotYaw()) {
            this.squidCap.setTargetRotYaw(targYaw);
            this.newPacketRequired = true;
        }
    }

    public double getTargRotPitch() { return this.squidCap.getTargetRotPitch(); }

    public double getTargRotYaw() { return this.squidCap.getTargetRotYaw(); }

    public boolean getBlasting() {
        if(this.squidCap != null) {
            return this.squidCap.getBlasting();
        }
        return false;
    }

    public void setBlasting(boolean b) {
        if(b != this.squidCap.getBlasting()) {
            this.squidCap.setBlasting(b);
            this.newPacketRequired = true;
        }
    }

    public boolean getShaking() {
        return this.squidCap.getShaking();
    }

    public void setShaking(boolean b) {
        if(b != this.squidCap.getShaking()) {
            this.squidCap.setShaking(b);
            this.newPacketRequired = true;
        }
    }

    public int getShakeTicks() {
        return this.squidCap.getShakeTicks();
    }

    public void setShakeTicks(int ticks) {
        this.squidCap.setShakeTicks(ticks);
    }

    public boolean getBlastToStatue() { return this.squidCap.getBlastToStatue(); }

    public void setBlastToStatue(boolean blast) { this.squidCap.setBlastToStatue(blast); }

    public boolean getForcedBlast() { return this.squidCap.getForcedBlast(); }

    public byte getTargetNote(byte index) {
        return this.squidCap.getTargetNotes()[index];
    }
}
