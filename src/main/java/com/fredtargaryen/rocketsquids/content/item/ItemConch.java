package com.fredtargaryen.rocketsquids.content.item;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.client.event.ModEventClient;
import com.fredtargaryen.rocketsquids.client.render.armor.ConchWearableRenderer;
import com.fredtargaryen.rocketsquids.content.ModBlocks;
import com.fredtargaryen.rocketsquids.content.block.StatueBlock;
import com.fredtargaryen.rocketsquids.content.item.custom.GeoModArmorItem;
import com.fredtargaryen.rocketsquids.content.worldgen.StatueData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemConch extends GeoModArmorItem {
    public static final ArmorMaterial MATERIAL_CONCH = new ArmorMaterial() {

        @Override
        public int getDurabilityForType(@NotNull Type type) {
            return 0;
        }

        @Override
        public int getDefenseForType(@NotNull Type type) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_GENERIC;
        }

        @Override
        public @Nullable Ingredient getRepairIngredient() {
            return null;
        }

        @Override
        public @NotNull String getName() {
            return DataReference.MODID + ":conch_item_1";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };

    public ItemConch(Item.Properties properties) {
        super(MATERIAL_CONCH, Type.HELMET, properties);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level worldIn,
            @NotNull Player playerIn,
            @NotNull InteractionHand handIn
    ) {
        if (worldIn.isClientSide && !playerIn.isCrouching()) ModEventClient.openConchClient((byte) 1);
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level worldIn = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        if(!worldIn.isClientSide && Objects.requireNonNull(player).isCrouching()) {
            BlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (block == ModBlocks.BLOCK_STATUE.get()) {
                if (iblockstate.getValue(BlockStateProperties.FACING) == Direction.UP) {
                    StatueData.forWorld(worldIn).removeStatue(pos);
                    if (facing == Direction.NORTH) {
                        worldIn.setBlockAndUpdate(pos, iblockstate.setValue(BlockStateProperties.FACING, Direction.NORTH));
                        context.getItemInHand().grow(-1);
                        ((StatueBlock) block).dispenseGift(worldIn, pos, facing);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                if (!iblockstate.getMaterial().isReplaceable()) {
                    pos = pos.relative(facing);
                }

                ItemStack itemstack = context.getItemInHand();

                BlockPlaceContext blockContext = new BlockPlaceContext(context);
                if (!itemstack.isEmpty() && player.mayUseItemAt(pos, facing, itemstack) && blockContext.canPlace()) {
                    Vec3 hitVec = context.getClickLocation();
                    float hitX = (float) hitVec.x;
                    float hitY = (float) hitVec.y;
                    float hitZ = (float) hitVec.z;
                    BlockState conchstate = ModBlocks.BLOCK_CONCH.get().getStateForPlacement(blockContext);

                    if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, conchstate)) {
                        BlockState iblockstate1 = worldIn.getBlockState(pos);
                        SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                        worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        itemstack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
        else {
            assert context.getPlayer() != null;
            this.use(context.getLevel(), context.getPlayer(), InteractionHand.MAIN_HAND);
        }
        return InteractionResult.FAIL;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    @SuppressWarnings("unused")
    public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
        if (!world.setBlock(pos, newState, 11)) return false;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == ModBlocks.BLOCK_CONCH.get()) {
            ModBlocks.BLOCK_CONCH.get().setPlacedBy(world, pos, state, player, stack);

            if (player instanceof ServerPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, stack);
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
                    this.renderer = new ConchWearableRenderer();

                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }
}
