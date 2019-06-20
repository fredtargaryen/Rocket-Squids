package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class StatueBlock extends FallingBlock {
    private static final VoxelShape TALLBOX = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);

    public StatueBlock() {
        super(Block.Properties.create(Material.ROCK));
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockStateProperties.FACING, Direction.UP));
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item asItem() { return RocketSquidsBase.ITEM_STATUE; }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return TALLBOX;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    @Deprecated
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid() && worldIn.getBlockState(pos.up()).isAir(worldIn, pos);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        StatueManager.forWorld(worldIn).addStatue(pos);
    }

    @Override
    @Deprecated
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        StatueManager.forWorld(worldIn).removeStatue(pos);
    }

    public void dispenseGift(World world, BlockPos pos, Direction facing) {
        switch(facing) {
            case NORTH:
                ItemEntity squav = new ItemEntity(world,pos.getX() + 0.5D, pos.getY(), pos.getZ() - 1.0D);
                squav.setItem(RocketSquidsBase.SQUAVIGATOR.getDefaultInstance());
                //North is negative Z I think
                squav.setVelocity(0.0, 0.2, -0.1);
                world.func_217376_c(squav);
                ItemEntity squel = new ItemEntity(world,pos.getX() + 0.5D, pos.getY(), pos.getZ() - 1.0D);
                squel.setItem(RocketSquidsBase.SQUELEPORTER_INACTIVE.getDefaultInstance());
                squel.setVelocity(0.0, 0.2, -0.1);
                world.func_217376_c(squel);
                break;
            default:
                break;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}
