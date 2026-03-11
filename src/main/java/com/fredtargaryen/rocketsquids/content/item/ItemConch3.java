package com.fredtargaryen.rocketsquids.content.item;

import com.fredtargaryen.rocketsquids.client.event.ModClientHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemConch3 extends Item {
    public ItemConch3(Item.Properties properties) {
        super(properties);
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
        if(worldIn.isClientSide) ModClientHandler.openConchClient((byte) 3);
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @Nullable Level level,
            @NotNull List<Component> tooltipComponents,
            @NotNull TooltipFlag isAdvanced
    ) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("item.rocketsquids.conch_item_3.lore"));
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     * <p>
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack stack)
    {
        return true;
    }
}
