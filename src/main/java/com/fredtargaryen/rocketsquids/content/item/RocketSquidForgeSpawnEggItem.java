// Copyright 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.content.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

// This is a hacked version of ForgeSpawnEggItem that overrides spawnOffspringFromSpawnEgg so we can spawn a different entity,
// I did this because WaterAnimal is not an instanceof AgableMob which is a requirement for spawnOffspringFromSpawnEgg aswell
// as needing the baby rocket squid to be a separate entity since of course WaterAnimal is not an instanceof AgableMob -barnabeepickle 12/12/2025

public class RocketSquidForgeSpawnEggItem extends ForgeSpawnEggItem {
    private final Supplier<? extends EntityType<?>> babyTypeSupplier;

    public RocketSquidForgeSpawnEggItem(
            Supplier<? extends EntityType<? extends Mob>> adultType,
            Supplier<? extends EntityType<? extends Mob>> babyType,
            int backgroundColor,
            int highlightColor,
            Properties props
    ) {
        super(adultType, backgroundColor, highlightColor, props);
        this.babyTypeSupplier = babyType;
    }

    @Override
    public @NotNull Optional<Mob> spawnOffspringFromSpawnEgg(
            @NotNull Player player,
            @NotNull Mob mob,
            @NotNull EntityType<? extends Mob> entityType,
            @NotNull ServerLevel level,
            @NotNull Vec3 pos,
            ItemStack stack
    ) {
        if (!this.spawnsEntity(stack.getTag(), entityType)) {
            return Optional.empty();
        } else {
            Mob mobentity = (Mob) this.babyTypeSupplier.get().create(level);

            if (mobentity == null) {
                return Optional.empty();
            } else {
                if (!mobentity.isBaby()) {
                    return Optional.empty();
                } else {
                    mobentity.moveTo(pos.x(), pos.y(), pos.z(), 0.0F, 0.0F);
                    level.addFreshEntityWithPassengers(mobentity);
                    if (stack.hasCustomHoverName()) {
                        mobentity.setCustomName(stack.getHoverName());
                    }

                    // check if the player is in creative essentially
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    return Optional.of(mobentity);
                }
            }
        }
    }
}
