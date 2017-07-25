package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.client.particle.SquidFirework;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAIBlastOff;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAIGiveUp;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAIShake;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAISwimAround;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidCapData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntityRocketSquid extends EntityWaterMob
{
    ///////////////
    //Client only//
    ///////////////
    public float tentacleAngle;
    public float lastTentacleAngle;
    public boolean riderRotated;


    private boolean newPacketRequired;
    protected final ISquidCapability squidCap;
    protected boolean isBaby;
    protected short breedCooldown;

    //May have to remove and use capability instead
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.<Boolean>createKey(EntityRocketSquid.class, DataSerializers.BOOLEAN);

    public EntityRocketSquid(World par1World)
    {
        super(par1World);
        //Set size of bounding box. par1=length and width; par2=height.
        //Normal squids are 0.8F, 0.8F. Previous: 1.1F, 1.1F
        this.setSize(0.99F, 0.99F);
        this.squidCap = this.getCapability(RocketSquidsBase.SQUIDCAP, null);
        this.riderRotated = false;
        this.isBaby = false;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(SADDLED, Boolean.valueOf(false));
    }

    @Override
    public void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAIBlastOff(this));
        this.tasks.addTask(1, new EntityAIShake(this));
        this.tasks.addTask(2, new EntityAISwimAround(this, 0.35));
        this.tasks.addTask(3, new EntityAIGiveUp(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
    }

    public boolean isBaby()
    {
        return this.isBaby;
    }

    @Override
    protected Item getDropItem(){return RocketSquidsBase.nitroinksac;}

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
	@Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();

        //Do on client and server
        //Fraction of distance to target rotation to rotate by each server tick
        double rotateSpeed;
        if(this.inWater)
        {
            this.motionX *= 0.9;
            this.motionY *= 0.9;
            this.motionZ *= 0.9;
            rotateSpeed = 0.1;
        }
        else
        {
            if(this.recentlyHit > 0)
            {
                this.motionX = 0.0D;
                this.motionZ = 0.0D;
            }
            if (this.isPotionActive(MobEffects.LEVITATION))
            {
                this.motionY += 0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY;
            }
            else if (!this.hasNoGravity())
            {
                this.motionY -= 0.08D;
            }
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
            rotateSpeed = 0.15;
        }

        boolean onFire = false;
        if(this.isBurning() || this.isInLava())
        {
            onFire = true;
            this.squidCap.setForcedBlast(true);
            this.newPacketRequired = true;
        }
        if(onFire || this.squidCap.getForcedBlast())
        {
            this.playSound(RocketSquidsBase.blastoff, 1.0F, 1.0F);
            this.squidCap.setBlasting(true);
        }

        //Rotate towards target pitch
        double trp = this.squidCap.getTargetRotPitch();
        double rp = this.squidCap.getRotPitch();
        if(trp != rp)
        {
            //Squids rotate <= 180 degrees either way.
            //The squid can rotate out of the interval [-PI, PI].
            rp += (trp - rp) * rotateSpeed;
            this.squidCap.setRotPitch(rp);
            this.newPacketRequired = true;
        }

        //Rotate towards target yaw
        double trY = this.squidCap.getTargetRotYaw();
        double ry = this.squidCap.getRotYaw();
        if(trY != ry)
        {
            ry += (trY - ry) * rotateSpeed;
            this.squidCap.setRotYaw(ry);
            this.newPacketRequired = true;
        }

        if(this.world.isRemote)
        {
            //Client side
            //Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if(this.squidCap.getShaking())
            {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            }
            else if(this.squidCap.getBlasting())
            {
                //Tentacles quickly close up
                this.tentacleAngle = 0;
            }
            else
            {
                if(this.inWater)
                {
                    //If in water, tentacles oscillate normally
                    this.tentacleAngle = this.inWater ? (float) ((Math.PI / 6) + (MathHelper.sin((float) Math.toRadians(4 * (this.ticksExisted % 360))) * Math.PI / 6)) : 0;
                }
                else
                {
                    this.tentacleAngle = 0;
                }
            }
            if(this.squidCap.getBlasting())
            {
                if(this.inWater)
                {
                    double smallerX = this.posX - 0.25;
                    double largerX = this.posX + 0.25;
                    double smallerZ = this.posZ - 0.25;
                    double largerZ = this.posZ + 0.25;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, smallerX, this.posY, smallerZ, 0.0, 0.0, 0.0);
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, smallerX, this.posY, largerZ, 0.0, 0.0, 0.0);
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, largerX, this.posY, smallerZ, 0.0, 0.0, 0.0);
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, largerX, this.posY, largerZ, 0.0, 0.0, 0.0);
                }
                else
                {
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
                }
            }
        }
        else
        {
            if(this.breedCooldown > 0)
            {
                --this.breedCooldown;
            }
            if(this.newPacketRequired)
            {
                MessageHandler.INSTANCE.sendToAllAround(new MessageSquidCapData(this.getPersistentID(), this.squidCap),
                        new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 64));
                this.newPacketRequired = false;
            }
        }
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward)
    {
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public boolean canPickUpLoot()
    {
        return false;
    }

    public void addForce(double n)
    {
        if(!this.world.isRemote) {
            double rp = this.squidCap.getRotPitch();
            double ry = this.squidCap.getRotYaw();
            this.motionY += n * Math.cos(rp);
            double horizontalForce = n * Math.sin(rp);
            this.motionZ += horizontalForce * Math.cos(ry);
            this.motionX += horizontalForce * -Math.sin(ry);
            this.isAirBorne = true;
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound()
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

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if(!this.isBaby && !this.world.isRemote)
        {
            ItemStack stack = player.getHeldItem(hand);
            if(stack == ItemStack.EMPTY)
            {
                if (this.getSaddled() && !this.isBeingRidden())
                {
                    player.startRiding(this);
                    return true;
                }
            }
            else
            {
                Item i = stack.getItem();
                if(i == Items.FLINT_AND_STEEL)
                {
                    stack.damageItem(1, player);
                    this.squidCap.setForcedBlast(true);
                    return true;
                }
                else if(i == Items.SADDLE)
                {
                    if(!this.getSaddled()) {
                        stack.damageItem(1, player);
                        this.setSaddled(true);
                    }
                    player.startRiding(this);
                    return true;
                }
                else if(i == Items.FEATHER && this.hasVIPRider())
                {
                    this.setShaking(true);
                    return true;
                }
                else
                {
                    if (this.getSaddled() && !this.isBeingRidden())
                    {
                        player.startRiding(this);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void explode()
    {
        if(!this.world.isRemote) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F, false);
            int noSacs = 3 + this.rand.nextInt(3);
            int noTubes = 2 + this.rand.nextInt(3);
            for (int x = 0; x < noSacs; ++x) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(RocketSquidsBase.nitroinksac));
                entityitem.motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                entityitem.motionY = -0.2;
                entityitem.motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                this.world.spawnEntity(entityitem);
            }
            for (int x = 0; x < noTubes; ++x) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(RocketSquidsBase.turbotube));
                entityitem.motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                entityitem.motionY = -0.2;
                entityitem.motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
                this.world.spawnEntity(entityitem);
            }
        }
        this.setDead();
    }

    @Override
    public void setDead()
    {
        if(this.world.isRemote && this.squidCap.getForcedBlast())
        {
            this.doFireworkParticles();
        }
        Entity passenger = this.getControllingPassenger();
        if(passenger != null)
        {
            this.removePassenger(passenger);
        }
        super.setDead();
    }

    @SideOnly(Side.CLIENT)
    private void doFireworkParticles()
    {
        ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
        effectRenderer.addEffect(new SquidFirework(
                (WorldClient) this.world, this.posX, this.posY, this.posZ, effectRenderer));
    }

    /**
     * Applies a velocity to the entities (unless they're riding), to push them away from each other.
     */
    public void applyEntityCollision(Entity obstacle)
    {
        Entity passenger = this.getControllingPassenger();
        if(passenger == null || passenger != obstacle)
        {
            //Obstacle is not the rider, so apply collision
            if (!obstacle.noClip && !this.noClip)
            {
                if(!this.world.isRemote && !this.isBaby && obstacle instanceof EntityRocketSquid && !((EntityRocketSquid)obstacle).isBaby && this.breedCooldown == 0)
                {
                    this.breedCooldown = 3600;
                    EntityBabyRocketSquid baby = new EntityBabyRocketSquid(this.world);
                    baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                    this.world.spawnEntity(baby);
                }
                double xDist = obstacle.posX - this.posX;
                double zDist = obstacle.posZ - this.posZ;
                double yDist = obstacle.posY - this.posY;
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
    protected void updateLeashedState()
    {
        super.updateLeashedState();

        if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().world == this.world)
        {
            Entity entity = this.getLeashedToEntity();
            float f = this.getDistanceToEntity(entity);

            if (f > 8.0F)
            {
                this.motionX = 0.0F;
                this.motionY = 0.0F;
                this.motionZ = 0.0F;
            }

            if (f > 6.0F)
            {
                double d0 = (entity.posX - this.posX) / (double)f;
                double d1 = (entity.posY - this.posY) / (double)f;
                double d2 = (entity.posZ - this.posZ) / (double)f;
                this.motionX += d0 * Math.abs(d0) * 0.4D;
                this.motionY += d1 * Math.abs(d1) * 0.4D;
                this.motionZ += d2 * Math.abs(d2) * 0.4D;
            }
            else
            {
                this.tasks.enableControlFlag(1);
            }
        }
    }

    //////////////////
    //RIDING METHODS//
    //////////////////

    @Override
    protected void addPassenger(Entity p)
    {
        if(this.getPassengers().size() == 0)
        {
            super.addPassenger(p);
            if(this.world.isRemote)
            {
                MinecraftForge.EVENT_BUS.register(this);
            }
        }
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        List passengers = this.getPassengers();
        if(passengers.isEmpty())
        {
            return null;
        }
        else {
            return this.getPassengers().get(0);
        }
    }

    //Later check rider name
    public boolean hasVIPRider()
    {
        if(!this.isBaby)
        {
            Entity passenger = this.getControllingPassenger();
            if (passenger != null && passenger instanceof EntityPlayer) {
                //return true;
                return passenger.getName().equals("Djymne");
            }
        }
        return false;
    }

    /**
     * Checks if squid pitch is less than -45 degrees and more than -135 degrees.
     * Between these angles the player would appear to hit the ground first so the player should be hurt.
     */
    @Override
    public void fall(float dist, float damMult)
    {
        if(Math.sin(this.squidCap.getRotPitch()) < -0.7071067811865)
        {
            super.fall(dist, damMult);
        }
    }

    /**
     * Can assume e is the passenger.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void applyOrientationToEntity(Entity e)
    {
        if(!this.hasVIPRider()) {
            float yawDifference = (float) ((this.squidCap.getRotYaw() - this.squidCap.getPrevRotYaw()) * 180 / Math.PI);
            e.prevRotationYaw += yawDifference;
            e.rotationYaw += yawDifference;
            e.setRotationYawHead(e.rotationYaw);
        }
    }

    /**
     * Should keep the passenger on, or at least around, the squid's back.
     */
    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger)) {
            passenger.setPosition(this.posX, this.posY + 0.355, this.posZ);
        }
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force)
    {
        return false;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return this.getPassengers().size() == 0;
    }

    public boolean getSaddled()
    {
        return ((Boolean)this.dataManager.get(SADDLED)).booleanValue();
    }

    /**
     * Set or remove the saddle of the squid.
     */
    private void setSaddled(boolean saddled)
    {
        if (saddled)
        {
            this.dataManager.set(SADDLED, Boolean.valueOf(true));
        }
        else
        {
            this.dataManager.set(SADDLED, Boolean.valueOf(false));
        }
    }

    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
    {
        super.dropEquipment(wasRecentlyHit, lootingModifier);

        if (this.getSaddled())
        {
            this.dropItem(Items.SADDLE, 1);
        }
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    @Override
    public boolean canRiderInteract()
    {
        return true;
    }

    /**
     * If the rider should be dismounted from the entity when the entity goes under water
     *
     * @param rider The entity that is riding
     * @return if the entity should be dismounted when under water
     */
    @Override
    public boolean shouldDismountInWater(Entity rider)
    {
        return false;
    }

    @Override
    public void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
        if (this.getBlasting())
        {
            passenger.motionX += this.motionX * 1.5;
            passenger.motionY += this.motionY * 1.5;
            passenger.motionZ += this.motionZ * 1.5;
        }
        if(this.world.isRemote)
        {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    /////////////////
    //CLIENT EVENTS//
    /////////////////
    /**
     * Add transformations to put player on back of squid.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void addRotation(RenderPlayerEvent.Pre event)
    {
        EntityPlayer p = event.getEntityPlayer();
        if(this.isPassenger(p))
        {
            this.riderRotated = true;
            GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.08F, 0.0F);
            double prevPitch_r = this.squidCap.getPrevRotPitch();
            double pitch_r = this.squidCap.getRotPitch();
			float partialTick = event.getPartialRenderTick();
            double exactPitch_r = prevPitch_r + (pitch_r - prevPitch_r) * partialTick;
            double exactPitch_d = exactPitch_r * 180 / Math.PI;
			double yaw_r = this.squidCap.getRotYaw();
            GlStateManager.rotate((float) (exactPitch_d - 90.0F),   (float) Math.cos(yaw_r), 0.0F, (float) Math.sin(yaw_r));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void removeRotation(RenderPlayerEvent.Post event)
    {
        if(this.riderRotated)
        {
            GlStateManager.popMatrix();
            this.riderRotated = false;
        }
    }

    ///////
    //NBT//
    ///////
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Saddle", this.getSaddled());
        compound.setShort("Breed Cooldown", this.breedCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setSaddled(compound.getBoolean("Saddle"));
        this.breedCooldown = compound.getShort("Breed Cooldown");
    }

    public void pointToWhereFlying()
    {
        if(!(Math.abs(this.motionY) < 0.0785 && this.motionX == 0.0 && this.motionZ == 0.0)) {
            //The aim is to find the local z movement to decide if the squid should pitch backwards or forwards.
            //The global z movement is given by this.motionZ.
            //In addForce, this.motionZ is given by horizontalForce * cos(yaw).
            //By rearranging, horizontalForce = this.motionZ / cos(yaw).
            //This is the amount by which the squid is moving along its own z axis (forwards or backwards).
            double speed = this.motionZ / Math.cos(this.squidCap.getRotYaw());
            this.setTargetRotPitch(Math.PI / 2 - Math.atan2(this.motionY, speed));
        }
    }

    //CAPABILITY METHODS
    public boolean getBlasting()
    {
        return this.squidCap.getBlasting();
    }

    public void setBlasting(boolean b)
    {
        if(b != this.squidCap.getBlasting()) {
            this.squidCap.setBlasting(b);
            this.newPacketRequired = true;
        }
    }

    public boolean getShaking()
    {
        return this.squidCap.getShaking();
    }

    public void setShaking(boolean b)
    {
        if(b != this.squidCap.getShaking()) {
            this.squidCap.setShaking(b);
            this.newPacketRequired = true;
        }
    }

    public double getPrevRotPitch()
    {
        return this.squidCap.getPrevRotPitch();
    }

    public double getPrevRotYaw()
    {
        return this.squidCap.getPrevRotYaw();
    }

    public double getRotPitch()
    {
        return this.squidCap.getRotPitch();
    }

    public double getRotYaw()
    {
        return this.squidCap.getRotYaw();
    }

    public void setTargetRotPitch(double targPitch)
    {
        if(targPitch != this.squidCap.getTargetRotPitch()) {
            this.squidCap.setTargetRotPitch(targPitch);
            this.newPacketRequired = true;
        }
    }

    public void setTargetRotYaw(double targYaw)
    {
        if (targYaw != this.squidCap.getTargetRotYaw()) {
            this.squidCap.setTargetRotYaw(targYaw);
            this.newPacketRequired = true;
        }
    }

    public double getTargRotPitch() { return this.squidCap.getTargetRotPitch(); }

    public double getTargRotYaw() { return this.squidCap.getTargetRotYaw(); }

    public boolean getForcedBlast() { return this.squidCap.getForcedBlast(); }
}