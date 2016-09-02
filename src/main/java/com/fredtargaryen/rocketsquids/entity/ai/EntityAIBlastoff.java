package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBlastOff extends EntityAIBase
{
    private EntityRocketSquid squid;
    private boolean blastStarted;
    private boolean horizontal;

    public EntityAIBlastOff(EntityRocketSquid ers)
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
            if((this.horizontal && Math.abs(this.squid.motionX) < 0.006 && Math.abs(this.squid.motionZ) < 0.006) ||
                    (!this.horizontal && Math.abs(this.squid.motionY) < 0.006))
            {
                //Squid has blasted but slowed down, i.e. end of blast
                this.squid.setShaking(false);
                this.squid.setBlasting(false);
                this.blastStarted = false;
                if(this.squid.getForcedBlast())
                {
                    this.squid.explode();
                }
            }
        }
        else
        {
            //Blast has not started yet
            this.squid.setShaking(false);
            this.squid.addForce(2.875);
            this.horizontal = Math.abs(this.squid.motionY) < 0.05;
            this.blastStarted = true;
        }
    }
}
