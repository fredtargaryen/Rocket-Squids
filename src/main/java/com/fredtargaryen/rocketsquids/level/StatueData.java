// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.config.CommonConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.*;

public class StatueData extends SavedData {
    /**
     * Each element is an array of 5 ints.
     * Chunks are grouped into n*n "chunk areas" where n is the STATUE_FREQUENCY config variable.
     * The first 2 ints are the chunk area x and z
     * The other 3 ints are the statue base block x, y and z
     */
    private List<List<Integer>> statues;

    public static final SavedDataType<StatueData> ID = new SavedDataType<>(
            // The identifier of the saved data
            // Used as the path within the level's `data` folder
            DataReference.getIdentifier("statues"),
            // The initial constructor
            StatueData::new,
            // The codec used to serialize the data
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(Codec.list(Codec.INT)).fieldOf("statues").forGetter(sd -> sd.statues)
            ).apply(instance, StatueData::new))
    );

    public StatueData() {
        super();
        this.statues = new ArrayList<>();
    }

    public StatueData(List<List<Integer>> coordLists) {
        this.statues = coordLists;
    }

    public static StatueData create() {
        return new StatueData();
    }

    public static StatueData forLevel(Level level) {
        ServerLevel serverLevel = Objects.requireNonNull(level.getServer()).getLevel(level.dimension());
        assert serverLevel != null;
        SavedDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(ID);
    }

    /**
     * Store a statue position in level data. Called by worldgen
     *
     * @param statuePos Expected to be a list of 5 ints: chunk group x, chunk group y, BlockPos x, BlockPos y and BlockPos z
     */
    public void addStatue(List<Integer> statuePos) {
        for (List<Integer> next : this.statues) {
            if (next.get(2) == statuePos.get(2) && next.get(3) == statuePos.get(3) && next.get(4) == statuePos.get(4)) {
                // Exists in data but update calculated chunk area coords in case they changed
                next.set(0, statuePos.get(0));
                next.set(1, statuePos.get(1));
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
        this.addStatue(Arrays.asList(
                posToChunkArea(x),
                posToChunkArea(z),
                x,
                pos.getY(),
                z));
    }

    /**
     * Remove a statue position from level data
     *
     * @param statuePos Expected to be a list of 5 ints: chunk group x, chunk group y, BlockPos x, BlockPos y and BlockPos z
     */
    public void removeStatue(List<Integer> statuePos) {
        Iterator<List<Integer>> iter = this.statues.iterator();
        while (iter.hasNext()) {
            List<Integer> next = iter.next();
            if (next.get(2) == statuePos.get(2) && next.get(3) == statuePos.get(3) && next.get(4) == statuePos.get(4)) {
                iter.remove();
                setDirty();
            }
        }
    }

    public List<Integer> getNearestStatuePos(double x, double y, double z) {
        if (this.statues.isEmpty()) return null;
        List<Integer> minloc = Arrays.asList(0, 0, 0, 0, 0);
        double minDistance = Double.POSITIVE_INFINITY;
        for (List<Integer> nextLoc : this.statues) {
            double nextXDist = nextLoc.get(2) - x;
            double nextYDist = nextLoc.get(3) - y;
            double nextZDist = nextLoc.get(4) - z;
            double nextDist = Math.sqrt(nextXDist * nextXDist + nextYDist * nextYDist + nextZDist * nextZDist);
            if (nextDist < minDistance) {
                minDistance = nextDist;
                minloc = nextLoc;
            }
        }
        return minloc;
    }

    public List<Integer> getChunkArea(int chunkX, int chunkZ) {
        for (List<Integer> i : this.statues) {
            if (i.get(0) == chunkX && i.get(1) == chunkZ) {
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
        return (int) Math.floor(posToChunk(pos) / (double) CommonConfig.STATUE_FREQUENCY);
    }
}
