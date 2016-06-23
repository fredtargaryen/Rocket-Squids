package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAIRandomMove;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageRSProps;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class EntityRocketSquid extends EntityWaterMob
{
    private int ticksInPhase;
    private int phaseLength;

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

    private float randomMotionVecX;
    private float randomMotionVecY;
    private float randomMotionVecZ;

    public float randomMotionSpeed;
    public float field_70871_bB;

    private Phase phase;

    private NBTTagCompound extraProperties;

    public enum Phase
    {
        SWIM,
        SHAKE,
        BLAST
    }

    public EntityRocketSquid(World par1World)
    {
        super(par1World);
        //sets size of bounding box. par1=length and width; par2=height.
        //Normal squids are 0.95F, 0.95F.
        this.setSize(0.95F, 0.95F);
        this.phase = Phase.SWIM;
        this.ticksInPhase = 0;
        this.extraProperties = new NBTTagCompound();
        if(!par1World.isRemote)
        {
            this.phaseLength = (int) (100 + this.getRNG().nextFloat() * 100);
            MessageRSProps m = new MessageRSProps();
            m.setProps(this.getEntityId(), this.getExtraProperties());
            MessageHandler.INSTANCE.sendToAllAround(m, new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimension(), this.posX, this.posY, this.posZ, 32.00D));
        }
        this.tasks.addTask(0, new EntityAIRandomMove(this));
    }

    public void setRandomMotionVectors(float x, float y, float z)
    {
        this.randomMotionVecX = x;
        this.randomMotionVecY = y;
        this.randomMotionVecZ = z;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
    }

    public boolean getBlasting()
    {
        return this.phase == Phase.SHAKE || this.phase == Phase.BLAST;
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
        //Client
        if(this.worldObj.isRemote)
        {
            /**
             * Handles tentacle angles
             */
            this.lastTentacleAngle = this.tentacleAngle;
            switch (this.phase) {
                case SHAKE:
                    //Tentacles stick out at 60 degrees
                    this.tentacleAngle = (float) Math.PI / 3;
                    break;
                case BLAST:
                    //Tentacles quickly close up
                    this.tentacleAngle = this.ticksInPhase > 100 ? 0 : (float) ((Math.PI / 3) * ((float) (100 - this.ticksInPhase) / 100));
                    break;
                default:
                    //If in water, tentacles oscillate normally
                    this.tentacleAngle = this.inWater ? (float) ((Math.PI / 6) + (MathHelper.sin((float) Math.toRadians(4 * (this.ticksExisted % 360))) * Math.PI / 6)) : 0;
                    break;
            }

            /**
             * Handles spinning
             */
            this.prevSpin = this.currentSpin;
            if(this.currentSpin == this.targetSpin)
            {
                this.ticksOfCurrentSpin = 0;
                //Keeps the spin value within range of -pi to pi
                if(this.currentSpin > Math.PI)
                {
                    this.currentSpin -= Math.PI;
                }
                else if(this.currentSpin < -Math.PI)
                {
                    this.currentSpin += Math.PI;
                }
                //1 in 10 chance of starting a new spin whenever it's not spinning - may have to increase number
                if(this.rand.nextInt(10) == 0)
                {
                    this.canDoNewSpin = true;
                }
            }
            else
            {
                //Changes the spin value appropriately
                //All spins should take 45 ticks so ticksOfCurrentSpin should be multiplied by 2
                ++this.ticksOfCurrentSpin;
                this.currentSpin = (float)(Math.sin(Math.toRadians((double)(this.ticksOfCurrentSpin * 2))) * (this.targetSpin - this.prevSpin) + this.prevSpin);
            }
            if(this.canDoNewSpin)
            {
                //Sets up "source and destination" spin values.
                //The squid can go 180 degrees around in either direction in one spin
                this.prevTargetSpin = this.currentSpin;
                this.targetSpin = this.rand.nextFloat() * (float)(Math.PI);
                if(this.rand.nextBoolean())
                {
                    this.targetSpin *= -1;
                }
                this.canDoNewSpin = false;
                this.ticksOfCurrentSpin = 0;
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

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    @Override
    public boolean canPickUpLoot()
    {
        return false;
    }

    public Phase getPhase()
    {
        return this.phase;
    }

    public boolean isMovingRandomly()
    {
        return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    @Override
    public boolean isInWater()
    {
        return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0D, -0.6000000238418579D, 0.0D), Material.WATER, this);
    }

    public NBTTagCompound getExtraProperties()
    {
        this.extraProperties.setInteger("tip", this.ticksInPhase);
        this.extraProperties.setInteger("pl", this.phaseLength);
        this.extraProperties.setFloat("s", this.currentSpin);
        this.extraProperties.setFloat("rmvx", this.randomMotionVecX);
        this.extraProperties.setFloat("rmvy", this.randomMotionVecY);
        this.extraProperties.setFloat("rmvz", this.randomMotionVecZ);
        this.extraProperties.setFloat("rms", this.randomMotionSpeed);
        this.extraProperties.setFloat("wtfisthis", this.field_70871_bB);
        byte b;
        switch(this.phase)
        {
            case BLAST:
                b = 0;
                break;
            case SHAKE:
                b = 1;
                break;
            default:
                b = 2;
                break;
        }
        this.extraProperties.setByte("phase", b);
        return this.extraProperties;
    }

    public void setExtraProperties(NBTTagCompound newProps)
    {
        this.extraProperties = newProps;
        this.ticksInPhase = this.extraProperties.getInteger("tip");
        this.phaseLength = this.extraProperties.getInteger("pl");
        this.currentSpin = this.extraProperties.getFloat("s");
        this.randomMotionVecX = this.extraProperties.getFloat("rmvx");
        this.randomMotionVecY = this.extraProperties.getFloat("rmvy");
        this.randomMotionVecZ = this.extraProperties.getFloat("rmvz");
        this.randomMotionSpeed = this.extraProperties.getFloat("rms");
        this.field_70871_bB = this.extraProperties.getFloat("wtfisthis");
        byte b = this.extraProperties.getByte("phase");
        switch(b)
        {
            case 0:
                this.phase = Phase.BLAST;
                break;
            case 1:
                this.phase = Phase.SHAKE;
                break;
            default:
                this.phase = Phase.SWIM;
                break;
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
}
