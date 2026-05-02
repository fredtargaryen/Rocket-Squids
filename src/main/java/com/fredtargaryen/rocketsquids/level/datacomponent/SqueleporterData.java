package com.fredtargaryen.rocketsquids.level.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public record SqueleporterData(CompoundTag entityData, CompoundTag attachmentData) {
    /**
     * This codec reads/writes squeleporter data to/from disk
     */
    public static final Codec<SqueleporterData> DISK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("entityData").forGetter(SqueleporterData::entityData),
                    CompoundTag.CODEC.fieldOf("attachmentData").forGetter(SqueleporterData::attachmentData)
            ).apply(instance, SqueleporterData::new));
}
