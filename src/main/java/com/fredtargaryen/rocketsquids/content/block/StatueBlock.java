package com.fredtargaryen.rocketsquids.content.block;

import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.content.worldgen.StatueData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class StatueBlock extends FallingBlock implements SimpleWaterloggedBlock {
    private static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape TALLBOX = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StatueBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public @NotNull Item asItem() { return RocketSquidsBase.ITEM_STATUE.get(); }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter worldIn,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return TALLBOX;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.getStateDefinition().any()
                .setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            @NotNull Direction direction,
            @NotNull BlockState neighborState,
            @NotNull LevelAccessor world,
            @NotNull BlockPos pos,
            @NotNull BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(
            @NotNull BlockState state,
            LevelReader worldIn,
            BlockPos pos
    ) {
        return worldIn.getBlockState(pos.below()).getMaterial().isSolid() && !worldIn.getBlockState(pos.above()).getMaterial().isSolid();
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void setPlacedBy(
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @Nullable LivingEntity placer,
            @NotNull ItemStack stack
    ) {
        assert placer != null;
        Direction facing = placer.getDirection();
        switch(facing) {
            case NORTH:
                level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.NORTH));
                break;
            case SOUTH:
                level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.SOUTH));
                break;
            case WEST:
                level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.WEST));
                break;
            default:
                level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.EAST));
                break;
        }
        if(!level.isClientSide) {
            StatueData.forWorld(level).addStatue(pos);
        }
    }

    @Override
    @Deprecated
    public void onPlace(
            @NotNull BlockState state,
            @NotNull Level worldIn,
            @NotNull BlockPos pos,
            @NotNull BlockState newState,
            boolean isMoving
    ) {
        super.onPlace(state, worldIn, pos, newState, isMoving);
        StatueData.forWorld(worldIn).removeStatue(pos);
    }

    public void dispenseGift(Level world, BlockPos pos, Direction facing) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        //Play some kind of wonderful "you found treasure" chord
        //Going with B4, D5 and F#5 (the minor chord makes it foreboding)
        world.playSound(null, pos, ModSounds.CONCH_NOTES[23],SoundSource.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos, ModSounds.CONCH_NOTES[26],SoundSource.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos, ModSounds.CONCH_NOTES[30],SoundSource.BLOCKS, 1.0F, 1.0F);
        if (Objects.requireNonNull(facing) == Direction.NORTH) {
            ItemEntity squav = new ItemEntity(world, x + 0.5D, y + 0.5D, z - 0.5D, RocketSquidsBase.SQUAVIGATOR.get().getDefaultInstance());
            // North is negative Z I think
            squav.setDeltaMovement(0.0, 0.05, -0.1);
            world.addFreshEntity(squav);
            ItemEntity squel = new ItemEntity(world, x + 0.5D, y + 0.5D, z - 0.5D, RocketSquidsBase.SQUAVIGATOR.get().getDefaultInstance());
            squel.setDeltaMovement(0.0, 0.05, -0.1);
            world.addFreshEntity(squel);
        }
    }

    /**
     * Remove its location. It can add a new one when it lands
     */
    @Override
    protected void falling(FallingBlockEntity fallingEntity) {
        BlockPos startPos = fallingEntity.blockPosition();
        BlockState startState = fallingEntity.level.getBlockState(startPos);
        if(startState.getValue(BlockStateProperties.FACING) == Direction.UP) {
            StatueData.forWorld(fallingEntity.level).removeStatue(startPos);
        }
    }

    /**
     * Add a new location for where it ended up
     */
    @Override
    public void onLand(
            @NotNull Level worldIn,
            @NotNull BlockPos pos,
            BlockState fallingState,
            @NotNull BlockState hitState,
            @NotNull FallingBlockEntity fallingBlock
    ) {
        if(fallingState.getValue(BlockStateProperties.FACING) == Direction.UP) {
            StatueData.forWorld(worldIn).addStatue(pos);
        }
    }
}
