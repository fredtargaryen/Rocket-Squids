package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.ai.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityRocketSquid extends EntityWaterMob
{
    public float tentacleAngle;
    public float lastTentacleAngle;

    private boolean canDoNewSpin;
    //DEGREES
    private int ticksOfCurrentSpin;

    //RADIANS
    public float currentSpin;
    public float prevSpin;
    public float prevTargetSpin;
    public float targetSpin;

    private double targetRotationPitch;
    private double targetRotationYaw;

    private boolean shaking;
    private boolean blasting;

    public EntityRocketSquid(World par1World)
    {
        super(par1World);
        //Set size of bounding box. par1=length and width; par2=height.
        //Normal squids are 0.95F, 0.95F.
        this.setSize(0.95F, 1.4F);
    }

    @Override
    public void initEntityAI()
    {
        //this.tasks.addTask(0, new EntityAIBlastOff(this));
        //this.tasks.addTask(1, new EntityAIShake(this));
        this.tasks.addTask(2, new EntityAISwimAround(this));
        this.tasks.addTask(3, new EntityAIGiveUp(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
    }

    public boolean getBlasting()
    {
        return this.blasting;
    }

    public void setBlasting(boolean b)
    {
        this.blasting = b;
    }

    public boolean getShaking()
    {
        return this.shaking;
    }

    public void setShaking(boolean b)
    {
        this.shaking = b;
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
        if(this.worldObj.isRemote)
        {
            //Client side
            //Handles tentacle angles
            this.lastTentacleAngle = this.tentacleAngle;
            if(this.shaking)
            {
                //Tentacles stick out at 60 degrees
                this.tentacleAngle = (float) Math.PI / 3;
            }
            else if(this.blasting)
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
//            if(this.phase != SHAKE)
//            {
//            //Handles spinning
//            this.prevSpin = this.currentSpin;
//            if(this.currentSpin == this.targetSpin)
//            {
//                this.ticksOfCurrentSpin = 0;
//                //Keeps the spin value within range of -pi to pi
//                if(this.currentSpin > Math.PI)
//                {
//                    this.currentSpin -= Math.PI;
//                }
//                else if(this.currentSpin < -Math.PI)
//                {
//                    this.currentSpin += Math.PI;
//                }
//                //1 in 10 chance of starting a new spin whenever it's not spinning - may have to increase number
//                if(this.rand.nextInt(10) == 0)
//                {
//                    this.canDoNewSpin = true;
//                }
//            }
//            else
//            {
//                //Changes the spin value appropriately
//                //All spins should take 45 ticks so ticksOfCurrentSpin should be multiplied by 2
//                ++this.ticksOfCurrentSpin;
//                this.currentSpin = (float)(Math.sin(Math.toRadians((double)(this.ticksOfCurrentSpin * 2))) * (this.targetSpin - this.prevSpin) + this.prevSpin);
//            }
//            if(this.canDoNewSpin)
//            {
//                //Sets up "source and destination" spin values.
//                //The squid can go 180 degrees around in either direction in one spin
//                this.prevTargetSpin = this.currentSpin;
//                this.targetSpin = this.rand.nextFloat() * (float)(Math.PI);
//                if(this.rand.nextBoolean())
//                {
//                    this.targetSpin *= -1;
//                }
//                this.canDoNewSpin = false;
//                this.ticksOfCurrentSpin = 0;
//            }
        }
        else
        {
            //Fraction of distance to target rotation to rotate by each server tick
            double rotateSpeed;
            if(this.inWater)
            {
                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
                rotateSpeed = 0.4;
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
                rotateSpeed = 0.8;
            }

            //Correct pitch to be within [-PI, PI] if necessary.
            //This may involve the sign change discussed below.
            if(this.rotationPitch < -Math.PI)
            {
                this.rotationPitch += Math.PI * 2;
            }
            else if(this.rotationPitch > Math.PI)
            {
                this.rotationPitch -= Math.PI * 2;
            }
            this.prevRotationPitch = this.rotationPitch;
            //Rotate towards target pitch
            if(this.targetRotationPitch != this.rotationPitch)
            {
                //A squid can rotate clockwise or anticlockwise to its target; this code picks the shortest
                //direction and rotates that way. One of the directions will take the rotation out of the
                //interval [-Math.PI, Math.PI]; a sign change of the rotation would be required to put the
                //rotation back in that interval.
                double distanceWithoutSignChange = Math.abs(this.targetRotationPitch - this.rotationPitch);
                if(distanceWithoutSignChange <= Math.PI)
                {
                    //Rotating in the shorter direction will not involve a sign change.
                    if(this.targetRotationPitch > this.rotationPitch)
                    {
                        this.rotationPitch += distanceWithoutSignChange * rotateSpeed;
                    }
                    else
                    {
                        this.rotationPitch -= distanceWithoutSignChange * rotateSpeed;
                    }
                }
                else
                {
                    //Rotating in the shorter direction will involve a sign change.
                    if(this.targetRotationPitch > this.rotationPitch)
                    {
                        this.rotationPitch -= distanceWithoutSignChange * rotateSpeed;
                    }
                    else
                    {
                        this.rotationPitch += distanceWithoutSignChange * rotateSpeed;
                    }
                }
            }

            //Same code as above but for yaw
            if(this.rotationYaw < -Math.PI)
            {
                this.rotationYaw += Math.PI * 2;
            }
            else if(this.rotationYaw > Math.PI)
            {
                this.rotationYaw -= Math.PI * 2;
            }
            this.prevRotationYaw = this.rotationYaw;
            if(this.targetRotationYaw != this.rotationYaw)
            {
                double distanceWithoutSignChange = Math.abs(this.targetRotationYaw - this.rotationYaw);
                if(distanceWithoutSignChange <= Math.PI)
                {
                    if(this.targetRotationYaw > this.rotationYaw)
                    {
                        this.rotationYaw += distanceWithoutSignChange * rotateSpeed;
                    }
                    else
                    {
                        this.rotationYaw -= distanceWithoutSignChange * rotateSpeed;
                    }
                }
                else
                {
                    if(this.targetRotationYaw > this.rotationYaw)
                    {
                        this.rotationYaw -= distanceWithoutSignChange * rotateSpeed;
                    }
                    else
                    {
                        this.rotationYaw += distanceWithoutSignChange * rotateSpeed;
                    }
                }
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
        this.motionY = n * Math.cos(this.rotationPitch);
        double horizontalForce = n * Math.sin(this.rotationPitch);
        this.motionZ = horizontalForce * Math.cos(this.rotationYaw);
        this.motionX = horizontalForce * Math.sin(this.rotationYaw);
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
            if(stack == null) {
                this.addForce(1.0);
            }
            else
            {
                this.addForce(3.0);
            }
        }
        return true;
    }

    public void setTargetRotationPitch(double targPitch)
    {
        this.targetRotationPitch = targPitch;
    }

    public void setTargetRotationYaw(double targYaw)
    {
        this.targetRotationYaw = targYaw;
    }
}
