package com.fredtargaryen.rocketsquids.content.block;

import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.content.ModItems;
import com.fredtargaryen.rocketsquids.content.worldgen.StatueData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;


public class StatueBlock extends Block {
    public StatueBlock(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, DOUBLE_BLOCK_HALF);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public @NotNull Item asItem() {
        return ModItems.ITEM_STATUE_CLOSED.get();
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            @NotNull Direction direction,
            @NotNull BlockState neighborState,
            @NotNull LevelAccessor level,
            @NotNull BlockPos pos,
            @NotNull BlockPos neighborPos
    ) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(DOUBLE_BLOCK_HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(DOUBLE_BLOCK_HALF) != doubleBlockHalf
                    ? state.setValue(FACING, neighborState.getValue(FACING))
                    .setValue(OPEN, neighborState.getValue(OPEN))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            DoubleBlockHalf doubleblockhalf = state.getValue(DOUBLE_BLOCK_HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(blockpos, blockstate1, 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockPos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(OPEN, context.getItemInHand().getItem() == ModItems.ITEM_STATUE_OPEN.get())
                    .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER || blockState.is(this);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void setPlacedBy(
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @Nullable LivingEntity placer,
            @NotNull ItemStack stack
    ) {
        if (!level.isClientSide) {
            level.setBlock(pos.above(), state.setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
            StatueData.forWorld(level).addStatue(pos);
        }
    }

    /**
     * Should only be called by ItemConch
     */
    public void dispenseGift(Level level, BlockPos pos, Direction facing) {
        //Play some kind of wonderful "you found treasure" chord
        //Going with B4, D5 and F#5 (the minor chord makes it foreboding)
        level.playSound(null, pos, ModSounds.CONCH_NOTES[23], SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, pos, ModSounds.CONCH_NOTES[26], SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, pos, ModSounds.CONCH_NOTES[30], SoundSource.BLOCKS, 1.0F, 1.0F);
        Vec3 treasureVelocity = switch (facing) {
            case EAST -> new Vec3(0.1, 0.05, 0.0);
            case NORTH -> new Vec3(0.0, 0.05, -0.1);
            case SOUTH -> new Vec3(0.0, 0.05, 0.1);
            default -> new Vec3(-0.1, 0.05, 0.0);
        };
        BlockPos treasureSpawnPos = pos.relative(facing);
        int x = treasureSpawnPos.getX();
        int y = treasureSpawnPos.getY();
        int z = treasureSpawnPos.getZ();
        ItemEntity squel = new ItemEntity(level, x + 0.5D, y + 0.5D, z + 0.5D, ModItems.SQUELEPORTER_INACTIVE.get().getDefaultInstance());
        squel.setDeltaMovement(treasureVelocity);
        level.addFreshEntity(squel);
    }
}
