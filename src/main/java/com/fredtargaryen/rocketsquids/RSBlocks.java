// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.block.ConchBlock;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static net.minecraft.world.level.block.Blocks.STONE;

public class RSBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);

    // Register all blocks here
    public static final DeferredHolder<Block, ConchBlock> CONCH = BLOCKS.register("conch",
            () -> new ConchBlock(Block.Properties.of().mapColor(MapColor.SAND).noCollision())
    );
    public static final DeferredHolder<Block, StatueBlock> STATUE = BLOCKS.register("statue",
            () -> new StatueBlock(BlockBehaviour.Properties.ofFullCopy(STONE).noOcclusion())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
