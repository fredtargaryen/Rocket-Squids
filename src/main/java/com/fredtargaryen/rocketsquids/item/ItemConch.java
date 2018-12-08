package com.fredtargaryen.rocketsquids.item;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.block.BlockStatue;
import com.fredtargaryen.rocketsquids.client.model.ModelConch;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConch extends ItemArmor {
    public static final ArmorMaterial MATERIAL_CONCH = EnumHelper.addArmorMaterial("material_conch", DataReference.MODID + ":conch", 2, new int[] {0, 0, 0, 0}, 10, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

    public ItemConch() {
        super(MATERIAL_CONCH, 1, EntityEquipmentSlot.HEAD);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote && !playerIn.isSneaking()) RocketSquidsBase.proxy.openConchClient((byte) 1);
        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
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
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && player.isSneaking()) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (block == RocketSquidsBase.blockStatue) {
                if (iblockstate.getValue(BlockStatue.ACTIVATION) == EnumFacing.DOWN) {
                    if (facing == EnumFacing.NORTH) {
                        worldIn.setBlockState(pos, iblockstate.withProperty(BlockStatue.ACTIVATION, EnumFacing.NORTH));
                        player.getHeldItem(hand).grow(-1);
                        ((BlockStatue) block).dispenseGift(worldIn, pos, facing);
                        return EnumActionResult.SUCCESS;
                    }
                }
            } else {
                if (!block.isReplaceable(worldIn, pos)) {
                    pos = pos.offset(facing);
                }

                ItemStack itemstack = player.getHeldItem(hand);

                if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(RocketSquidsBase.blockConch, pos, false, facing, (Entity) null)) {
                    IBlockState conchstate = RocketSquidsBase.blockConch.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);

                    if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, conchstate)) {
                        IBlockState iblockstate1 = worldIn.getBlockState(pos);
                        SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                        worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        itemstack.shrink(1);
                    }

                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == RocketSquidsBase.blockConch) {
            RocketSquidsBase.blockConch.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        if(stack != null) {
            if (stack.getItem() == this) {
                EntityEquipmentSlot type = ((ItemArmor) stack.getItem()).armorType;
                ModelBiped armorModel;
                if (type == EntityEquipmentSlot.HEAD) {
                    armorModel = RocketSquidsBase.proxy.getConchModel();
                    armorModel.bipedHead.showModel = defaultModel.bipedHead.showModel;
                    armorModel.bipedHeadwear.showModel = armorSlot == EntityEquipmentSlot.HEAD;

                    armorModel.isSneak = defaultModel.isSneak;
                    armorModel.isRiding = defaultModel.isRiding;
                    armorModel.isChild = defaultModel.isChild;
                    armorModel.rightArmPose = defaultModel.rightArmPose;
                    armorModel.leftArmPose = defaultModel.leftArmPose;

                    return armorModel;
                }
            }
        }
        return null;
    }
}
