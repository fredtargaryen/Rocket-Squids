// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level;

import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class StatueData extends SavedData {
    /**
     * Each element is an array of 5 ints.
     * Chunks are grouped into n*n "chunk areas" where n is the STATUE_FREQUENCY config variable.
     * The first 2 ints are the chunk area x and z
     * The other 3 ints are the statue base block x, y and z
     */
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
        for (int i = 0; i < amount; ++i) {
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
        for (int i = 0; i < amount; ++i) {
            tag.putIntArray(String.valueOf(i), this.statues.get(i));
        }
        return tag;
    }

    /**
     * Store a statue position in level data. Called by worldgen
     *
     * @param statuePos Expected to be a list of 5 ints: chunk group x, chunk group y, BlockPos x, BlockPos y and BlockPos z
     */
    public void addStatue(int[] statuePos) {
        for (int[] next : this.statues) {
            if (next[2] == statuePos[2] && next[3] == statuePos[3] && next[4] == statuePos[4]) {
                // Exists in data but update calculated chunk area coords in case they changed
                next[0] = statuePos[0];
                next[1] = statuePos[1];
                setDirty();
                return;
            }
        }
        // New position so add to data
        this.statues.add(statuePos);
        setDirty();
    }

    /**
     * Store a statue position in level data. Called when manually placing a statue block
     *
     * @param pos the position of the statue being placed
     */
    public void addStatue(BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        this.addStatue(new int[]{
                posToChunkArea(x),
                posToChunkArea(z),
                x,
                pos.getY(),
                z
        });
    }

    /**
     * Remove a statue position from level data
     *
     * @param statuePos Expected to be a list of 5 ints: chunk group x, chunk group y, BlockPos x, BlockPos y and BlockPos z
     */
    public void removeStatue(int[] statuePos) {
        Iterator<int[]> iter = this.statues.iterator();
        while (iter.hasNext()) {
            int[] next = iter.next();
            if (next[2] == statuePos[2] && next[3] == statuePos[3] && next[4] == statuePos[4]) {
                iter.remove();
                setDirty();
            }
        }
    }

    public int[] getNearestStatuePos(double x, double y, double z) {
        if (this.statues.isEmpty()) return null;
        int[] minloc = {0, 0, 0, 0, 0};
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
        for (int[] i : this.statues) {
            if (i[0] == chunkX && i[1] == chunkZ) {
                return i;
            }
        }
        return null;
    }

    //////////////////////////////////////
    //Block and chunk pos helper methods//
    //////////////////////////////////////

    /**
     * Get the chunk coordinate corresponding to a given BlockPos coordinate
     *
     * @param pos the x or z value of a block position
     * @return the chunk x or z coordinate the BlockPos should be in
     */
    public static int posToChunk(int pos) {
        return (int) Math.floor(pos / 16.0);
    }

    /**
     * Get the chunk are coordinate corresponding to a given BlockPos coordinate
     *
     * @param pos the x or z value of a block position
     * @return the chunk area x or z coordinate the BlockPos should be in
     */
    public static int posToChunkArea(int pos) {
        return (int) Math.floor(posToChunk(pos) / (double) GeneralConfig.STATUE_FREQUENCY.get());
    }
}
