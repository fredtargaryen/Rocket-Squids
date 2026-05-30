// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.item;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RSBlocks;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import com.fredtargaryen.rocketsquids.level.StatueData;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.renderer.GeoArmorRenderer;
import com.google.common.base.Suppliers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER;

public class ConchItem extends GeoModArmorItem {
    public static final ResourceKey<EquipmentAsset> CONCH_EQUIPMENT_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, DataReference.getIdentifier("conch"));

    public ConchItem(Item.Properties properties) {
        super(properties
                .component(
                        DataComponents.EQUIPPABLE,
                        Equippable.builder(ArmorType.HELMET.getSlot())
                                .setEquipSound(SoundEvents.ARMOR_EQUIP_NAUTILUS)
                                .setAsset(CONCH_EQUIPMENT_ASSET)
                                .build()));
    }

    /**
     * Called when the equipped item is right-clicked, but not when interacting with a block.
     */
    @Override
    public @NotNull InteractionResult use(
            Level level,
            @NotNull Player playerIn,
            @NotNull InteractionHand handIn
    ) {
        if (level.isClientSide() && !playerIn.isCrouching()) ClientHandler.openConchClient((byte) 1);
        return InteractionResult.PASS;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        assert player != null;
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (player.isCrouching()) {
            // Assume just trying to place the conch block
            if (!level.isClientSide()) {
                if (!state.canBeReplaced()) {
                    pos = pos.relative(facing);
                }

                ItemStack itemstack = context.getItemInHand();

                BlockPlaceContext blockContext = new BlockPlaceContext(context);
                if (!itemstack.isEmpty() && player.mayUseItemAt(pos, facing, itemstack) && blockContext.canPlace()) {
                    Vec3 hitVec = context.getClickLocation();
                    float hitX = (float) hitVec.x;
                    float hitY = (float) hitVec.y;
                    float hitZ = (float) hitVec.z;
                    BlockState conchstate = RSBlocks.CONCH.get().getStateForPlacement(blockContext);

                    if (!conchstate.canSurvive(level, pos)) return InteractionResult.FAIL;

                    if (placeBlockAt(itemstack, player, level, pos, facing, hitX, hitY, hitZ, conchstate)) {
                        BlockState iblockstate1 = level.getBlockState(pos);
                        SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, level, pos, player);
                        level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        itemstack.shrink(1);
                    }

                    return InteractionResult.CONSUME;
                }
            }
        } else {
            boolean shouldInsertConch = block == RSBlocks.STATUE.get() && !state.getValue(OPEN);
            // If the player has right-clicked a statue, activate it
            if (!level.isClientSide() && shouldInsertConch) {
                if (state.getValue(DOUBLE_BLOCK_HALF) == UPPER) {
                    BlockState stateBelow = level.getBlockState(pos.below());
                    if (stateBelow.getBlock() == RSBlocks.STATUE.get() && stateBelow.getValue(DOUBLE_BLOCK_HALF) == LOWER) {
                        pos = pos.below();
                        state = stateBelow;
                    }
                }
                StatueData.forLevel(level).removeStatue(Arrays.asList(
                        0, 0, pos.getX(), pos.getY(), pos.getZ()
                ));
                level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
                context.getItemInHand().grow(-1);
                ((StatueBlock) block).dispenseGifts(level, pos, state.getValue(HORIZONTAL_FACING));
                return InteractionResult.CONSUME;
            } else if (level.isClientSide() && !shouldInsertConch) {
                this.use(level, player, context.getHand());
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.FAIL;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack  The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side   The side the player (or machine) right-clicked on.
     */
    @SuppressWarnings("unused")
    public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
        if (!world.setBlock(pos, newState, 11)) return false;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == RSBlocks.CONCH.get()) {
            RSBlocks.CONCH.get().setPlacedBy(world, pos, state, player, stack);

            if (player instanceof ServerPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, stack);
        }

        return true;
    }

    /**
     * Registers the renderer for the worn conch
     */
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<GeoArmorRenderer<ConchItem, ?>> renderer = Suppliers.memoize(() -> new GeoArmorRenderer<>(ConchItem.this));

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                return this.renderer.get();
            }
        });
    }
}
