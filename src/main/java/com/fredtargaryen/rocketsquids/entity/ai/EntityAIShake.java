package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIShake extends EntityAIBase
{
    private EntityRocketSquid squid;
    private int ticksLeft;

    public EntityAIShake(EntityRocketSquid ers)
    {
        super();
        this.squid = ers;
        this.ticksLeft = -1;
    }

    @Override
    public boolean shouldExecute()
    {
        return this.squid.getShaking();
    }

    @Override
    public void updateTask()
    {
        if(this.ticksLeft == -1)
        {
            //No shake in progress; start one
            this.ticksLeft = 15 + this.squid.getRNG().nextInt(45);
        }
        else if(this.ticksLeft == 0)
        {
            this.squid.setBlasting(true);
            this.ticksLeft = -1;
        }
        else
        {
            --this.ticksLeft;
        }
    }
}
