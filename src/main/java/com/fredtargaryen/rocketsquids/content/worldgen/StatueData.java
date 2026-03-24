// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static com.fredtargaryen.rocketsquids.DataReference.*;

public class StatueData extends SavedData {
    //5 integers; 2 integers for index of 100x100 chunk area; 3 integers for exact coords
    private ArrayList<int[]> statues;

    public StatueData() {
        super();
        this.statues = new ArrayList<>();
    }

    public StatueData create() {
        return new StatueData();
    }

    public StatueData load(CompoundTag tag) {
        this.statues = new ArrayList<>();
        int amount = tag.getInt("amount");
        for(int i = 0; i < amount; ++i) {
            this.statues.add(tag.getIntArray(String.valueOf(i)));
        }
        return new StatueData();
    }

    public static StatueData forWorld(Level world) {
        StatueData data = new StatueData();
        return data.forLevel(world);
    }

    public StatueData forLevel(Level world) {
        ServerLevel serverWorld = Objects.requireNonNull(world.getServer()).getLevel(world.dimension());
        assert serverWorld != null;
        DimensionDataStorage storage = serverWorld.getDataStorage();
        return storage.computeIfAbsent(this::load, this::create, MODID);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        int amount = this.statues.size();
        tag.putInt("amount", this.statues.size());
        for(int i = 0; i < amount; ++i) {
            tag.putIntArray(String.valueOf(i), this.statues.get(i));
        }
        return tag;
    }

    public void addStatue(BlockPos pos) {
        int targetX = pos.getX();
        int targetY = pos.getY();
        int targetZ = pos.getZ();
        boolean found = false;
        for (int[] next : this.statues) {
            if (next[2] == targetX && next[3] == targetY && next[4] == targetZ) {
                found = true;
                break;
            }
        }
        if(!found) {
            this.statues.add(new int[] { targetX / 1600, targetZ / 1600, targetX, targetY, targetZ });
            setDirty();
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
                setDirty();
            }
        }
    }

    public int[] getNearestStatuePos(double x, double y, double z) {
        int[] minloc = new int[] {-1, -1, -1, -1, -1};
        double minDistance = Double.POSITIVE_INFINITY;
        for (int[] nextLoc : this.statues) {
            double nextXDist = nextLoc[2] - x;
            double nextYDist = nextLoc[3] - y;
            double nextZDist = nextLoc[4] - z;
            double nextDist = Math.sqrt(nextXDist * nextXDist + nextYDist * nextYDist + nextZDist * nextZDist);
            if (nextDist < minDistance) {
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
