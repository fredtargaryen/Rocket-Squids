// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.block.ConchBlock;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static net.minecraft.world.level.block.Blocks.STONE;

public class RSBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    // Register all blocks here
    public static final RegistryObject<ConchBlock> BLOCK_CONCH = BLOCKS.register("conch",
            () -> new ConchBlock(Block.Properties.of().mapColor(MapColor.SAND).noCollission())
    );
    public static final RegistryObject<StatueBlock> BLOCK_STATUE = BLOCKS.register("statue",
            () -> new StatueBlock(BlockBehaviour.Properties.copy(STONE).noOcclusion())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
