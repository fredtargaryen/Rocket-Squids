package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.entity.ai.EntityAIGiveUp;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAISwimAround;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityBabyRocketSquid extends EntityRocketSquid
{
    public EntityBabyRocketSquid(World w)
    {
        super(w);
        this.setSize(0.5F, 0.5F);
        if(w.isRemote)
        {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.isBaby = true;
    }

    @Override
    public void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimAround(this, 0.15));
        this.tasks.addTask(1, new EntityAIGiveUp(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Saddle", false);
    }
}
