package com.fredtargaryen.rocketsquids.level.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public record SqueleporterData(CompoundTag squidData) {
    /**
     * This codec reads/writes squeleporter data to/from disk
     */
    public static final Codec<SqueleporterData> DISK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("squidData").forGetter(SqueleporterData::squidData)
            ).apply(instance, SqueleporterData::new));
}
