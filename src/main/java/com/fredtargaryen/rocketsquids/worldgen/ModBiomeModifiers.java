package com.fredtargaryen.rocketsquids.worldgen;

import com.fredtargaryen.rocketsquids.worldgen.biomemodifiers.ModBlockDetailBiomeModifier;
import com.fredtargaryen.rocketsquids.worldgen.biomemodifiers.ModEntityBiomeModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

    public static RegistryObject<Codec<ModBlockDetailBiomeModifier>> BLOCK_DETAIL_MODIFIER = BIOME_MODIFIERS.register("block_detail", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModBlockDetailBiomeModifier::biomes),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(ModBlockDetailBiomeModifier::feature)
            ).apply(builder, ModBlockDetailBiomeModifier::new)));

    public static RegistryObject<Codec<ModEntityBiomeModifier>> ENTITY_MODIFIER = BIOME_MODIFIERS.register("entities", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModEntityBiomeModifier::biomes),
                    MobSpawnSettings.SpawnerData.CODEC.fieldOf("entity").forGetter(ModEntityBiomeModifier::spawnerData)
            ).apply(builder, ModEntityBiomeModifier::new)));

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIERS.register(eventBus);
    }
}
