package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.block.StatueBlock;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemConch extends ArmorItem {
    public static final IArmorMaterial MATERIAL_CONCH = new IArmorMaterial() {
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 2;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return null;
        }

        @Override
        public String getName() {
            return DataReference.MODID + ":conch";
        }

        @Override
        public float getToughness() {
            return 0;
        }
    };

    public ItemConch() {
        super(MATERIAL_CONCH, EquipmentSlotType.HEAD, new Item.Properties().group(RocketSquidsBase.SQUIDS_TAB).maxStackSize(4));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote && !playerIn.isSneaking()) RocketSquidsBase.proxy.openConchClient((byte) 1);
        return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
        //FOR CONCH ARMOUR DEBUGGING
//        System.out.println("Yaw = "+playerIn.rotationYaw);
//        System.out.println("Head Yaw = "+playerIn.getRotationYawHead());
//        Vec3d look = playerIn.getLookVec();
//        System.out.println("Math.atan2 = "+Math.atan2(look.z, look.x));
//        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World worldIn = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        if(!worldIn.isRemote && player.isSneaking()) {
            BlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (block == RocketSquidsBase.BLOCK_STATUE) {
                if (iblockstate.get(BlockStateProperties.FACING) == Direction.UP) {
                    StatueManager.forWorld(worldIn).removeStatue(pos);
                    if (facing == Direction.NORTH) {
                        worldIn.setBlockState(pos, iblockstate.with(BlockStateProperties.FACING, Direction.NORTH));
                        context.getItem().grow(-1);
                        ((StatueBlock) block).dispenseGift(worldIn, pos, facing);
                        return ActionResultType.SUCCESS;
                    }
                }
            } else {
                if (!block.getMaterial(worldIn.getBlockState(pos)).isReplaceable()) {
                    pos = pos.offset(facing);
                }

                ItemStack itemstack = context.getItem();

                BlockItemUseContext blockContext = new BlockItemUseContext(context);
                if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && blockContext.canPlace()) {
                    Vec3d hitVec = context.getHitVec();
                    float hitX = (float) hitVec.x;
                    float hitY = (float) hitVec.y;
                    float hitZ = (float) hitVec.z;
                    BlockState conchstate = RocketSquidsBase.BLOCK_CONCH.getStateForPlacement(blockContext);

                    if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, conchstate)) {
                        BlockState iblockstate1 = worldIn.getBlockState(pos);
                        SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                        worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        itemstack.shrink(1);
                    }

                    return ActionResultType.SUCCESS;
                } else {
                    return ActionResultType.FAIL;
                }
            }
        }
        else {
            this.onItemRightClick(context.getWorld(), context.getPlayer(), Hand.MAIN_HAND);
        }
        return ActionResultType.FAIL;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
        if (!world.setBlockState(pos, newState, 11)) return false;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == RocketSquidsBase.BLOCK_CONCH) {
            RocketSquidsBase.BLOCK_CONCH.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof ServerPlayerEntity)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
        }

        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, BipedModel defaultModel) {
        if(stack != null) {
            if (stack.getItem() == this) {
                EquipmentSlotType type = ((ArmorItem) stack.getItem()).getEquipmentSlot();
                BipedModel armorModel;
                if (type == EquipmentSlotType.HEAD) {
                    armorModel = RocketSquidsBase.proxy.getConchModel();
                    armorModel.field_78116_c.showModel = defaultModel.field_78116_c.showModel; //Head, I hope
                    armorModel.bipedHeadwear.showModel = armorSlot == EquipmentSlotType.HEAD;

                    armorModel.isSneak = defaultModel.isSneak;
                    armorModel.field_205061_a = defaultModel.field_205061_a; //Don't know what this is
                    armorModel.rightArmPose = defaultModel.rightArmPose;
                    armorModel.leftArmPose = defaultModel.leftArmPose;

                    return armorModel;
                }
            }
        }
        return null;
    }
}
