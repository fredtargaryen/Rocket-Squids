package com.fredtargaryen.rocketsquids.block;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
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
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getMaterial().isSolid();
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if(!worldIn.isRemote) {
            StatueManager.forWorld(worldIn).addStatue(pos);
        }
    }

    @Override
    @Deprecated
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        StatueManager.forWorld(worldIn).removeStatue(pos);
    }

    public void dispenseGift(World world, BlockPos pos, Direction facing) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        //Play some kind of wonderful "you found treasure" chord
        //Going with B4, D5 and F#5 (the minor chord makes it foreboding)
        world.playSound(null, pos, Sounds.CONCH_NOTES[23],SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos, Sounds.CONCH_NOTES[26],SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos, Sounds.CONCH_NOTES[30],SoundCategory.BLOCKS, 1.0F, 1.0F);
        switch(facing) {
            case NORTH:
                ItemEntity squav = new ItemEntity(world,x + 0.5D, y + 0.5D, z - 0.5D);
                squav.setItem(RocketSquidsBase.SQUAVIGATOR.getDefaultInstance());
                //North is negative Z I think
                squav.setVelocity(0.0, 0.05, -0.1);
                world.func_217376_c(squav);
                ItemEntity squel = new ItemEntity(world,x + 0.5D, y + 0.5D, z - 0.5D);
                squel.setItem(RocketSquidsBase.SQUELEPORTER_INACTIVE.getDefaultInstance());
                squel.setVelocity(0.0, 0.05, -0.1);
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

    /**
     * Remove its location. It can add a new one when it lands
     */
    @Override
    protected void onStartFalling(FallingBlockEntity fallingEntity) {
        BlockPos startPos = fallingEntity.getPosition();
        BlockState startState = fallingEntity.world.getBlockState(startPos);
        if(startState.get(BlockStateProperties.FACING) == Direction.UP) {
            StatueManager.forWorld(fallingEntity.world).removeStatue(startPos);
        }
    }

    /**
     * Add a new location for where it ended up
     */
    @Override
    public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
        if(fallingState.get(BlockStateProperties.FACING) == Direction.UP) {
            StatueManager.forWorld(worldIn).addStatue(pos);
        }
    }
}
