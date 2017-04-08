package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.client.model.RenderBabyRS;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAIGiveUp;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAISwimAround;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityBabyRocketSquid extends EntityRocketSquid
{
    public EntityBabyRocketSquid(World w)
    {
        super(w);
        this.setSize(0.4F, 0.4F);
        if(w.isRemote)
        {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.isBaby = true;
    }

    @Override
    public void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.taskEntries.clear();
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

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(this.ticksExisted > 72000)
        {
            if(!this.worldObj.isRemote)
            {
                this.setDead();
                EntityRocketSquid adult = new EntityRocketSquid(this.worldObj);
                adult.setLocationAndAngles(this.posX, this.posY, this.posZ, (float) this.squidCap.getRotYaw(), (float) this.squidCap.getRotPitch());
                this.worldObj.spawnEntityInWorld(adult);
            }
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {}

    @Override
    protected Item getDropItem(){return null;}
}
