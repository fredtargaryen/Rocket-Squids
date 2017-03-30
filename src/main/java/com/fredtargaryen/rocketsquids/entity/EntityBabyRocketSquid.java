package com.fredtargaryen.rocketsquids.entity;

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
        this.setSize(0.2F, 0.2F);
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
        if(this.entityAge == 180)
        {
            if(!this.world.isRemote)
            {
                this.setDead();
                EntityRocketSquid adult = new EntityRocketSquid(this.world);
                adult.setPositionAndRotation(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                this.world.spawnEntity(adult);
            }
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if(!this.world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
                Item i = stack.getItem();
                if(i == Items.APPLE)
                {
                    //Increase before offset
                    RenderBabyRS.BEFORE_ROT_OFFSET += 0.1F;
                }
                else if(i == Items.BEETROOT)
                {
                    //Decrease before offset
                    RenderBabyRS.BEFORE_ROT_OFFSET -= 0.1F;
                }
                else if(i == Items.COMPASS)
                {
                    //Increase after offset
                    RenderBabyRS.AFTER_ROT_OFFSET += 0.1F;
                }
                else if(i == Items.CLOCK)
                {
                    //Decrease after offset
                    RenderBabyRS.AFTER_ROT_OFFSET -= 0.1F;
                }
                System.out.println("'Before' offset: "+RenderBabyRS.BEFORE_ROT_OFFSET+"; 'After' offset: "+RenderBabyRS.AFTER_ROT_OFFSET);
            }
        }
        return true;
    }
}
