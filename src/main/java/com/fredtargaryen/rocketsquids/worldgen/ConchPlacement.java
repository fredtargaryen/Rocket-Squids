package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.config.GeneralConfig;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.SandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConchPlacement extends Placement<ConchPlacementConfig> {
    public ConchPlacement(Function<Dynamic<?>, ? extends ConchPlacementConfig> func) {
        super(func);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, ConchPlacementConfig placementConfig, BlockPos pos) {
        // First check the config to see if this dimension is allowed
        if(GeneralConfig.CONCH_USE_WHITELIST.get())
        {
            List<? extends String> allowedDimensions = GeneralConfig.CONCH_WHITELIST.get();
            if(!allowedDimensions.contains(world.getDimension().getType().getRegistryName().toString())) return Stream.empty();
        }
        else
        {
            List<? extends String> blockedDimensions = GeneralConfig.CONCH_BLACKLIST.get();
            if(blockedDimensions.contains(world.getDimension().getType().getRegistryName().toString())) return Stream.empty();
        }
        int maxConches = random.nextInt(3); //was 3
        int blockX = (pos.getX() / 16) * 16;
        int blockZ = (pos.getZ() / 16) * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos groundPos = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(conchX, 0, conchZ)).down();
            BlockState blockState = world.getBlockState(groundPos);
            if (blockState.getMaterial() == Material.SAND) {
                return Stream.of(groundPos.up());
            }
        }
        return Stream.empty();
    }
}
