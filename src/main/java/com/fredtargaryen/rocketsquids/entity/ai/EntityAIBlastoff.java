package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBlastOff extends EntityAIBase
{
    private EntityRocketSquid squid;
    private boolean blastStarted;

    public EntityAIBlastOff(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
        this.blastStarted = false;
    }

    //Change to isBurning() later
    @Override
    public boolean shouldExecute()
    {
        return this.squid.getBlasting() || this.squid.isInLava();
    }

    @Override
    public void updateTask()
    {
        if (this.blastStarted)
        {
            //The squid is part of the way through a blast
            if(Math.sqrt((this.squid.motionX * this.squid.motionX) + (this.squid.motionY * this.squid.motionY) +
                    (this.squid.motionZ * this.squid.motionZ)) < 0.05)
            {
                //Squid has blasted but slowed down, i.e. end of blast
                this.squid.setShaking(false);
                this.squid.setBlasting(false);
                this.blastStarted = false;
                if(this.squid.isBurning())
                {
                    //Explosion goes here
                    this.squid.setDead();
                }
            }
        }
        else
        {
            //Blast has not started yet
            this.squid.setShaking(false);
            this.squid.addForce(3.0);
            this.blastStarted = true;
        }
    }
}
