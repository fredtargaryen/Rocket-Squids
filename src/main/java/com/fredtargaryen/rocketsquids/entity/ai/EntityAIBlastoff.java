package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBlastOff extends EntityAIBase
{
    private EntityRocketSquid squid;

    public EntityAIBlastOff(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getBlasting() || this.squid.isInLava();
    }

    @Override
    public void updateTask()
    {
        if(this.squid.getShaking() || this.squid.isInLava())
        {
            //Squid is in lava or at end of shake, so should start blast
            this.squid.setShaking(false);
            this.squid.setBlasting(true);
            this.squid.addForce(3.0);
        }
        else if(Math.abs(this.squid.motionX) < 0.05 && Math.abs(this.squid.motionY) < 0.05 &&
                Math.abs(this.squid.motionZ) < 0.05)
        {
            //Squid has blasted but slowed down, i.e. end of blast
            this.squid.setBlasting(false);
        }
    }
}
