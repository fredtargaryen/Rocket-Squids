// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.block;

import com.fredtargaryen.rocketsquids.RSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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

public class ConchBlock extends Block {
    private static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape CONCH_NORTH = Block.box(3.5, 0, 5, 11.5, 3, 10);
    private static final VoxelShape CONCH_SOUTH = Block.box(4.5, 0, 6, 12.5, 3, 11);
    private static final VoxelShape CONCH_WEST = Block.box(5, 0, 4.5, 10, 3, 12.5);
    private static final VoxelShape CONCH_EAST = Block.box(6, 0, 3.5, 11, 3, 11.5);

    public ConchBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.getStateDefinition().any()
                .setValue(FACING, ctx.getNearestLookingDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
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

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public @NotNull Item asItem() {
        return RSItems.ITEM_CONCH.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter reader,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return this.getShape(state, reader, pos, context);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter reader,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case NORTH -> CONCH_NORTH;
            case SOUTH -> CONCH_SOUTH;
            case WEST -> CONCH_WEST;
            default -> CONCH_EAST;
        };
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void setPlacedBy(
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            LivingEntity placer,
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
    }
}
