package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBlastoff extends EntityAIBase
{
    private final EntityRocketSquid squid;
    private boolean blastStarted;
    private boolean horizontal;
    private double prevMotionX;
    private double prevMotionY;
    private double prevMotionZ;

    public EntityAIBlastoff(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.blastStarted = false;
        this.horizontal = true;
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getBlasting() || this.squid.getForcedBlast();
    }

    @Override
    public void updateTask()
    {
        if (this.blastStarted)
        {
            //The squid is part of the way through a blast
            if((this.horizontal
                    && this.motionHasPeaked(this.prevMotionX, this.squid.motionX)
                    && this.motionHasPeaked(this.prevMotionZ, this.squid.motionZ))
                    || (!this.horizontal && this.motionHasPeaked(this.prevMotionY, this.squid.motionY)))
            {
                //Squid has blasted but slowed down, i.e. end of blast
                this.squid.setShaking(false);
                this.squid.setBlasting(false);
                this.squid.isAirBorne = false;
                this.blastStarted = false;
                if(this.squid.getForcedBlast())
                {
                    this.squid.explode();
                }
            }
            else
            {
                this.squid.pointToWhereFlying();
            }
        }
        else
        {
            //Blast has not started yet
            this.squid.setShaking(false);
            this.squid.playSound(Sounds.BLASTOFF, 1.0F, 1.0F);
            this.squid.addForce(2.952);
            this.horizontal = Math.abs(this.squid.motionY) < 0.08;
            this.blastStarted = true;
        }
        this.prevMotionX = this.squid.motionX;
        this.prevMotionY = this.squid.motionY;
        this.prevMotionZ = this.squid.motionZ;
    }

    private boolean motionHasPeaked(double prev, double current)
    {
        return (prev >= 0 && current <= 0) || (prev <= 0 && current >= 0);
    }
}
