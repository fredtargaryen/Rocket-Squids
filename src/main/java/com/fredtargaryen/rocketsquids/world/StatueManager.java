package com.fredtargaryen.rocketsquids.world;

import com.fredtargaryen.rocketsquids.DataReference;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.Iterator;

public class StatueManager extends WorldSavedData {
    //5 integers; 2 integers for index of 100x100 chunk area; 3 integers for exact coords
    private ArrayList<int[]> statues;

    public StatueManager() {
        super(DataReference.MODID);
        this.statues = new ArrayList<>();
    }

    public static StatueManager forWorld(World world) {
        ServerWorld serverWorld = world.getServer().getWorld(world.getDimensionKey());
        DimensionSavedDataManager storage = serverWorld.getSavedData();
        return storage.getOrCreate(StatueManager::new, DataReference.MODID);
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.statues = new ArrayList<>();
        int amount = nbt.getInt("amount");
        for(int i = 0; i < amount; ++i) {
            this.statues.add(nbt.getIntArray(String.valueOf(i)));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        int amount = this.statues.size();
        compound.putInt("amount", this.statues.size());
        for(int i = 0; i < amount; ++i) {
            compound.putIntArray(String.valueOf(i), this.statues.get(i));
        }
        return compound;
    }

    public void addStatue(BlockPos pos) {
        int targetX = pos.getX();
        int targetY = pos.getY();
        int targetZ = pos.getZ();
        boolean found = false;
        Iterator<int[]> iter = this.statues.iterator();
        while(iter.hasNext()) {
            int[] next = iter.next();
            if(next[2] == targetX && next[3] == targetY && next[4] == targetZ) {
                found = true;
            }
        }
        if(!found) {
            this.statues.add(new int[] { targetX / 1600, targetZ / 1600, targetX, targetY, targetZ });
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
            if(next[2] == targetX && next[3] == targetY && next[4] == targetZ) {
                iter.remove();
                markDirty();
            }
        }
    }

    public int[] getNearestStatuePos(double x, double y, double z) {
        int[] minloc = new int[] {-1, -1, -1, -1, -1};
        double minDistance = Double.POSITIVE_INFINITY;
        Iterator<int[]> iter = this.statues.iterator();
        while(iter.hasNext()) {
            int[] nextLoc = iter.next();
            double nextXDist = nextLoc[2] - x;
            double nextYDist = nextLoc[3] - y;
            double nextZDist = nextLoc[4] - z;
            double nextDist = Math.sqrt(nextXDist * nextXDist + nextYDist * nextYDist + nextZDist * nextZDist);
            if(nextDist < minDistance) {
                minDistance = nextDist;
                minloc = nextLoc;
            }
        }
        return minloc;
    }

    public int[] getChunkArea(int chunkX, int chunkZ) {
        for(int[] i : this.statues) {
            if(i[0] == chunkX && i[1] == chunkZ) {
                return i;
            }
        }
        return null;
    }
}
