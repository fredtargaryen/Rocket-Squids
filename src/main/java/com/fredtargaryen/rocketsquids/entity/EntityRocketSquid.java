package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.ai.*;
import com.fredtargaryen.rocketsquids.entity.capability.ISquidCapability;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidCapData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class EntityRocketSquid extends EntityWaterMob
{
    public float tentacleAngle;
    public float lastTentacleAngle;

    private boolean newPacketRequired;

    private ISquidCapability squidCap;

    public EntityRocketSquid(World par1World)
    {
        super(par1World);
        //Set size of bounding box. par1=length and width; par2=height.
        //Normal squids are 0.8F, 0.8F.
        this.setSize(0.95F, 1.4F);
        this.squidCap = this.getCapability(RocketSquidsBase.SQUIDCAP, null);
    }

    @Override
    public void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAIBlastOff(this));
        this.tasks.addTask(1, new EntityAIShake(this));
        this.tasks.addTask(2, new EntityAISwimAround(this));
        this.tasks.addTask(3, new EntityAIGiveUp(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
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
            rotateSpeed = 0.15;
        }
        else
        {
            if (this.isPotionActive(MobEffects.LEVITATION))
            {
                this.motionY += 0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY;
            }
            else if (!this.func_189652_ae())
            {
                this.motionY -= 0.08D;
            }
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
            rotateSpeed = 0.2;
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

        if(this.worldObj.isRemote)
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
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, smallerX, this.posY, smallerZ, 0.0, 0.0, 0.0);
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, smallerX, this.posY, largerZ, 0.0, 0.0, 0.0);
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, largerX, this.posY, smallerZ, 0.0, 0.0, 0.0);
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, largerX, this.posY, largerZ, 0.0, 0.0, 0.0);
                }
                else
                {
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
                }
            }
        }
        else
        {
            //Server side
            if(this.newPacketRequired)
            {
                MessageHandler.INSTANCE.sendToAllAround(new MessageSquidCapData(this.getPersistentID(), this.squidCap),
                        new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 64));
                this.newPacketRequired = false;
            }
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    @Override
    public void moveEntityWithHeading(float par1, float par2)
    {
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public boolean canPickUpLoot()
    {
        return false;
    }

    public void addForce(double n)
    {
        double rp = this.squidCap.getRotPitch();
        double ry = this.squidCap.getRotYaw();
        this.motionY = n * Math.cos(rp);
        double horizontalForce = n * Math.sin(rp);
        this.motionZ = horizontalForce * Math.cos(ry);
        this.motionX = horizontalForce * -Math.sin(ry);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected SoundEvent getHurtSound()
    {
        return null;
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
    {
        if(!this.worldObj.isRemote)
        {
            if(stack != null) {
                Item i = stack.getItem();
                if (i == Items.FLINT_AND_STEEL)
                {
                    stack.damageItem(1, player);
                    this.setBlasting(true);
                    //this.setFire(10);
                }
                else if(i == Items.SADDLE)
                {
                    //TODO
                }
            }
        }
        return true;
    }

    public void explode()
    {
        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F, false);
        int noSacs = 3 + this.rand.nextInt(3);
        int noTubes = 2 + this.rand.nextInt(3);
        for(int x = 0; x < noSacs; ++x)
        {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(RocketSquidsBase.nitroinksac));
            entityitem.motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
            entityitem.motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
            this.worldObj.spawnEntityInWorld(entityitem);
        }
        for(int x = 0; x < noTubes; ++x)
        {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(RocketSquidsBase.turbotube));
            entityitem.motionX = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
            entityitem.motionZ = this.rand.nextDouble() * 1.5F * (this.rand.nextBoolean() ? 1 : -1);
            this.worldObj.spawnEntityInWorld(entityitem);
        }
//        NBTTagCompound onlyFirework = new NBTTagCompound();
//        //Set data in onlyFirework
//        onlyFirework.setBoolean("Flicker", true);
//        NBTTagList squidTag = new NBTTagList();
//        squidTag.addTag(onlyFirework);
//        NBTTagCompound finalCompound = new NBTTagCompound();
//        finalCompound.setTagList("Explosions", squidTag);
//        this.worldObj.makeFireworks(this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, finalCompound);
        this.setDead();
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
}
