// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GeoModArmorItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeoModArmorItem(ArmorMaterial armorMaterial, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(armorMaterial, type));
    }

    // Create our animation controller
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, 20, state -> {
//            // Apply our generic idle animation.
//            // When it plays is decided below
//            state.setAnimation(DefaultAnimations.IDLE);
//
//            // Gather some information about the entity wearing it
//            Entity entity = state.getData(DataTickets.ENTITY);
//
//            // Check if it is an armor stand
//            if (entity instanceof ArmorStand) {
//                // if so don't play
//                return PlayState.STOP;
//            } else {
//                // if not do play
//                return PlayState.CONTINUE;
//            }
//        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
