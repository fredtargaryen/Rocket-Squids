package com.fredtargaryen.rocketsquids.entity;

import com.fredtargaryen.rocketsquids.entity.ai.EntityAIGiveUp;
import com.fredtargaryen.rocketsquids.entity.ai.EntityAISwimAround;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityBabyRocketSquid extends EntityRocketSquid {
    public EntityBabyRocketSquid(World w) {
        super(w);
        this.setSize(0.4F, 0.4F);
        if(w.isRemote)
        {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.isBaby = true;
    }

    @Override
    public void initEntityAI() {
        super.initEntityAI();
        this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAISwimAround(this, 0.15));
        this.tasks.addTask(1, new EntityAIGiveUp(this));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return false;
    }

    @Override
    public boolean writeUnlessRemoved(NBTTagCompound compound) {
        super.writeUnlessRemoved(compound);
        compound.setBoolean("Saddle", false);
        return true;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void livingTick() {
        super.livingTick();
        if(this.ticksExisted > 72000) {
            if(!this.world.isRemote) {
                this.remove();
                EntityRocketSquid adult = new EntityRocketSquid(this.world);
                adult.setLocationAndAngles(this.posX, this.posY, this.posZ, (float) this.squidCap.getRotYaw(), (float) this.squidCap.getRotPitch());
                this.world.spawnEntity(adult);
            }
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {}

    @Override
    protected Item getDropItem(){return null;}
}
