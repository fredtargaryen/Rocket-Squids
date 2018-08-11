package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockConch extends Block {
    public BlockConch() {
        super(Material.PLANTS);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return RocketSquidsBase.itemConch;
    }

    /**
     * Indicate if a material is a normal solid opaque cube
     */
    @Deprecated
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }
}
