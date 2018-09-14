package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockConch extends Block {
    private static final AxisAlignedBB CONCH_AABB_EAST = new AxisAlignedBB(0.1875, 0.0, 0.125, 0.375, 0.125, 0.5);
    private static final AxisAlignedBB CONCH_AABB_SOUTH = new AxisAlignedBB(0.5, 0.0, 0.1875, 0.875, 0.125, 0.375);
    private static final AxisAlignedBB CONCH_AABB_WEST = new AxisAlignedBB(0.625, 0.0, 0.5, 0.8125, 0.125, 0.875);
    private static final AxisAlignedBB CONCH_AABB_NORTH = new AxisAlignedBB(0.125, 0.0, 0.625, 0.5, 0.125, 0.8125);

    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>()
    {
        public boolean apply(@Nullable EnumFacing p_apply_1_)
        {
            return p_apply_1_ != EnumFacing.DOWN && p_apply_1_ != EnumFacing.UP;
        }
    });

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

    ///////////////////////////////////////////////////////////////
    //These two are required so other blocks can render around it//
    ///////////////////////////////////////////////////////////////
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /////////////////////////////////////////////////////////////////////////
    //This is required so it won't be treated as a full cube when colliding//
    /////////////////////////////////////////////////////////////////////////
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch(state.getValue(FACING)) {
            case NORTH:
                return CONCH_AABB_NORTH;
            case SOUTH:
                return CONCH_AABB_SOUTH;
            case WEST:
                return CONCH_AABB_WEST;
            default:
                return CONCH_AABB_EAST;
        }
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    ////////////////////
    //BLOCKSTATE STUFF//
    ////////////////////
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch(meta) {
            case 2:
                return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH);
            case 3:
                return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
            case 4:
                return this.getDefaultState().withProperty(FACING, EnumFacing.WEST);
            default:
                return this.getDefaultState().withProperty(FACING, EnumFacing.EAST);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
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
                worldIn.setBlockState(pos, def.withProperty(FACING, EnumFacing.EAST));
                break;
            case SOUTH:
                worldIn.setBlockState(pos, def.withProperty(FACING, EnumFacing.WEST));
                break;
            case WEST:
                worldIn.setBlockState(pos, def.withProperty(FACING, EnumFacing.NORTH));
                break;
            default:
                worldIn.setBlockState(pos, def.withProperty(FACING, EnumFacing.SOUTH));
                break;
        }
    }
}
