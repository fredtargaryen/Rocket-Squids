// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.item;

import com.fredtargaryen.rocketsquids.RSArmorMaterials;
import com.fredtargaryen.rocketsquids.RSBlocks;
import com.fredtargaryen.rocketsquids.client.event.ClientHandler;
import com.fredtargaryen.rocketsquids.client.render.ConchOnHeadRenderer;
import com.fredtargaryen.rocketsquids.level.StatueData;
import com.fredtargaryen.rocketsquids.level.block.StatueBlock;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.ArrayList;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER;

public class ItemConch extends GeoModArmorItem {
    private final ItemAttributeModifiers emptyModifierMap;

    public ItemConch(Item.Properties properties) {
        super(RSArmorMaterials.CONCH, Type.HELMET, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        emptyModifierMap = new ItemAttributeModifiers(new ArrayList<>(), false);
    }

    /**
     * Called when the equipped item is right clicked, but not when interacting with a block.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level worldIn,
            @NotNull Player playerIn,
            @NotNull InteractionHand handIn
    ) {
        if (worldIn.isClientSide && !playerIn.isCrouching()) ClientHandler.openConchClient((byte) 1);
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
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
            if (!level.isClientSide) {
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
            // If the player has right-clicked a statue, activate it
            if (!level.isClientSide) {
                if (block == RSBlocks.STATUE.get()) {
                    if (!state.getValue(OPEN)) {
                        if (state.getValue(DOUBLE_BLOCK_HALF) == UPPER) {
                            BlockState stateBelow = level.getBlockState(pos.below());
                            if (stateBelow.getBlock() == RSBlocks.STATUE.get() && stateBelow.getValue(DOUBLE_BLOCK_HALF) == LOWER) {
                                pos = pos.below();
                                state = stateBelow;
                            }
                        }
                        StatueData.forLevel(level).removeStatue(new int[]{
                                0, 0, pos.getX(), pos.getY(), pos.getZ()
                        });
                        level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
                        context.getItemInHand().grow(-1);
                        ((StatueBlock) block).dispenseGifts(level, pos, state.getValue(HORIZONTAL_FACING));
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }

        if (level.isClientSide) {
            this.use(level, player, context.getHand());
            return InteractionResult.PASS;
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

    // Create our armor model/renderer for forge and return it
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new ConchOnHeadRenderer();

                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    /**
     * Removes the "When on head:" tooltip, which is too much of a giveaway
     */
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return emptyModifierMap;
    }
}
