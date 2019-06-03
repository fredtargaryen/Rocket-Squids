package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockConch extends Block {
    private static final VoxelShape CONCH_EAST = Block.makeCuboidShape(0.1875, 0.0, 0.125, 0.375, 0.125, 0.5);
    private static final VoxelShape CONCH_SOUTH = Block.makeCuboidShape(0.5, 0.0, 0.1875, 0.875, 0.125, 0.375);
    private static final VoxelShape CONCH_WEST = Block.makeCuboidShape(0.625, 0.0, 0.5, 0.8125, 0.125, 0.875);
    private static final VoxelShape CONCH_NORTH = Block.makeCuboidShape(0.125, 0.0, 0.625, 0.5, 0.125, 0.8125);

    public BlockConch() {
        super(Block.Properties.create(Material.PLANTS));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.WATERLOGGED);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item asItem() { return RocketSquidsBase.ITEM_CONCH; }

    /**
     * Allows other blocks to render around it.
     */
    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
        public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
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
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
     * not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     * @deprecated call via IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing) whenever possible.
     * Implementing/overriding is fine.
     */
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = placer.getHorizontalFacing();
        IBlockState def = this.getDefaultState();
        switch(facing) {
            case NORTH:
                worldIn.setBlockState(pos, def.with(BlockStateProperties.HORIZONTAL_FACING, EnumFacing.EAST));
                break;
            case SOUTH:
                worldIn.setBlockState(pos, def.with(BlockStateProperties.HORIZONTAL_FACING, EnumFacing.WEST));
                break;
            case WEST:
                worldIn.setBlockState(pos, def.with(BlockStateProperties.HORIZONTAL_FACING, EnumFacing.NORTH));
                break;
            default:
                worldIn.setBlockState(pos, def.with(BlockStateProperties.HORIZONTAL_FACING, EnumFacing.SOUTH));
                break;
        }
    }
}
