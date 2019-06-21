package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ConchBlock extends Block {
    private static final VoxelShape CONCH_EAST = Block.makeCuboidShape(3.0, 0.0, 2.0, 6.0, 2.0, 8.0);
    private static final VoxelShape CONCH_SOUTH = Block.makeCuboidShape(8.0, 0.0, 3.0, 14.0, 2.0, 6.0);
    private static final VoxelShape CONCH_WEST = Block.makeCuboidShape(10.0, 0.0, 8.0, 13.0, 2.0, 14.0);
    private static final VoxelShape CONCH_NORTH = Block.makeCuboidShape(2.0, 0.0, 10.0, 8.0, 2.0, 13.0);

    public ConchBlock() {
        super(Block.Properties.create(Material.SAND));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.WATERLOGGED);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item asItem() { return RocketSquidsBase.ITEM_CONCH; }

    @Override
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, reader, pos, context);
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        switch(state.get(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH:
                return CONCH_NORTH;
            case SOUTH:
                return CONCH_SOUTH;
            case WEST:
                return CONCH_WEST;
            default:
                return CONCH_EAST;
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction facing = placer.getHorizontalFacing();
        switch(facing) {
            case NORTH:
                worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
                break;
            case SOUTH:
                worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
                break;
            case WEST:
                worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
                break;
            default:
                worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
                break;
        }
    }
}
