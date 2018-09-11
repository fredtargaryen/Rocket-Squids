package com.fredtargaryen.rocketsquids.world;

import com.fredtargaryen.rocketsquids.DataReference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.Iterator;

public class StatueManager extends WorldSavedData {
    private ArrayList<int[]> statues;

    public StatueManager(String key) {
        super(key);
    }

    public static StatueManager forWorld(World world) {
        //Retrieves the StatueManager instance for the given world, creating it if necessary
        MapStorage storage = world.getPerWorldStorage();
        StatueManager data = (StatueManager) storage.getOrLoadData(StatueManager.class, DataReference.MODID);
        if(data == null) {
            FMLLog.warning("[ROCKETSQUIDS-SERVER] No statue data was found for this world. Creating new statue data.");
            data = new StatueManager(DataReference.MODID);
            storage.setData(DataReference.MODID, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.statues = new ArrayList<>();
        int amount = nbt.getInteger("amount");
        for(int i = 0; i < amount; ++i) {
            this.statues.add(nbt.getIntArray(String.valueOf("i")));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        int amount = this.statues.size();
        compound.setInteger("amount", this.statues.size());
        for(int i = 0; i < amount; ++i) {
            compound.setIntArray(String.valueOf(i), this.statues.get(i));
        }
        return null;
    }

    public void addStatue(BlockPos pos) {
        int targetX = pos.getX();
        int targetY = pos.getY();
        int targetZ = pos.getZ();
        boolean found = false;
        Iterator<int[]> iter = this.statues.iterator();
        while(iter.hasNext()) {
            int[] next = iter.next();
            if(next[0] == targetX && next[1] == targetY && next[2] == targetZ) {
                found = true;
            }
        }
        if(!found) {
            this.statues.add(new int[] {pos.getX(), pos.getY(), pos.getZ()});
            markDirty();
        }
    }

    public void removeStatue(BlockPos pos) {
        int targetX = pos.getX();
        int targetY = pos.getY();
        int targetZ = pos.getZ();
        Iterator<int[]> iter = this.statues.iterator();
        while(iter.hasNext()) {
            int[] next = iter.next();
            if(next[0] == targetX && next[1] == targetY && next[2] == targetZ) {
                iter.remove();
                markDirty();
            }
        }
    }
}
