package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class ConchGen implements IWorldGenerator {
    /**
     * Generate some world
     *
     * @param random the chunk specific {@link Random}.
     * @param chunkX the chunk X coordinate of this chunk.
     * @param chunkZ the chunk Z coordinate of this chunk.
     * @param world : additionalData[0] The minecraft {@link World} we're generating for.
     * @param chunkGenerator : additionalData[1] The {@link IChunkProvider} that is generating.
     * @param chunkProvider : additionalData[2] {@link IChunkProvider} that is requesting the world generation.
     *
     */
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int maxConches = random.nextInt(3); //was 3
        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        for (int conches = 0; conches < maxConches; ++conches) {
            int conchX = blockX + random.nextInt(16);
            int conchZ = blockZ + random.nextInt(16);
            BlockPos conchPos = world.getTopSolidOrLiquidBlock(new BlockPos(conchX, 0, conchZ)).down();
            IBlockState blockState = world.getBlockState(conchPos);
            if (blockState.getBlock() instanceof BlockSand) {
                world.setBlockState(conchPos.up(), RocketSquidsBase.blockConch.getDefaultState());
            }
            else if(blockState.getMaterial() == Material.WATER) {
                BlockPos down = conchPos.down();
                if (world.getBlockState(down.down()).getMaterial() == Material.SAND) {
                    world.setBlockState(down, RocketSquidsBase.blockConch.getDefaultState(), 2);
                }
            }
        }
    }
}
