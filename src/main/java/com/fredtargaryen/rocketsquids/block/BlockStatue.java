package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockStatue extends BlockFalling {
    private static final VoxelShape TALLBOX = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);

    public BlockStatue() {
        super(Block.Properties.create(Material.ROCK));
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockStateProperties.FACING, EnumFacing.UP));
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item asItem() { return RocketSquidsBase.ITEM_STATUE; }

    @Override
    @Deprecated
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return TALLBOX;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    @Deprecated
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isNormalCube() && worldIn.getBlockState(pos.up()).isAir(worldIn, pos);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        StatueManager.forWorld(worldIn).addStatue(pos);
    }

    @Override
    @Deprecated
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        StatueManager.forWorld(worldIn).removeStatue(pos);
    }

    public void dispenseGift(World world, BlockPos pos, EnumFacing facing) {
        switch(facing) {
            case NORTH:
                EntityItem squav = new EntityItem(world);
                squav.setItem(RocketSquidsBase.SQUAVIGATOR.getDefaultInstance());
                squav.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() - 1.0D);
                //North is negative Z I think
                squav.setVelocity(0.0, 0.2, -0.1);
                world.spawnEntity(squav);
                EntityItem squel = new EntityItem(world);
                squel.setItem(RocketSquidsBase.SQUELEPORTER_INACTIVE.getDefaultInstance());
                squel.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() - 1.0D);
                squel.setVelocity(0.0, 0.2, -0.1);
                world.spawnEntity(squel);
                break;
            default:
                break;
        }
    }

    /**
     * Allows other blocks to render around it.
     */
    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}
