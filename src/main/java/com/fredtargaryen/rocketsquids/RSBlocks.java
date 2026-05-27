// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import com.fredtargaryen.rocketsquids.level.block.ConchBlock;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;
import static net.minecraft.world.level.block.Blocks.STONE;

public class RSBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    // Register all blocks here
    public static final DeferredBlock<ConchBlock> CONCH = BLOCKS.register(
            "conch",
            registryName -> new ConchBlock(Block.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, registryName))
                    .mapColor(MapColor.SAND)
                    .noCollision())
    );
    public static final DeferredBlock<StatueBlock> STATUE = BLOCKS.register(
            "statue",
            registryName -> new StatueBlock(BlockBehaviour.Properties.ofFullCopy(STONE)
                    .setId(ResourceKey.create(Registries.BLOCK, registryName))
                    .noOcclusion())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
