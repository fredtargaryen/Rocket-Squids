package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.world.StatueManager;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class StatuePlacement extends Placement<StatuePlacementConfig> {
    public StatuePlacement(Function<Dynamic<?>, ? extends StatuePlacementConfig> func) {
        super(func);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, StatuePlacementConfig placementConfig, BlockPos pos) {
        StatueManager statueManager = StatueManager.forWorld((World) world);
        int chunkX = pos.getX() / 16;
        int chunkZ = pos.getZ() / 16;
        int chunkAreaX = chunkX / 100;
        int chunkAreaZ = chunkZ / 100;
        int[] statueLocation = statueManager.getChunkArea(chunkAreaX, chunkAreaZ);
        if(statueLocation == null) {
            //A statue location hasn't been decided for this chunk area. Decide one
            statueLocation = new int[] {chunkAreaX, chunkAreaZ,
                    //Random chunk in the 100x100 area
                    (chunkAreaX * 100 + random.nextInt(100))
                            //Random block in the 16x16 chunk
                            * 16 + random.nextInt(16),
                    random.nextInt(80) + 1,
                    (chunkAreaZ * 100 + random.nextInt(100)) * 16 + random.nextInt(16)};
            statueManager.addStatue(new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]));
        }
        if(chunkX == statueLocation[2] / 16 && chunkZ == statueLocation[4] / 16) {
            //The statue should go in this chunk. Put a statue in here
            BlockPos placePos = new BlockPos(statueLocation[2], statueLocation[3], statueLocation[4]);
            return Stream.of(placePos);
        }
        return Stream.empty();
    }
}
