package com.fredtargaryen.rocketsquids.level.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;

public record SqueleporterData(CompoundTag squidData) {
    /**
     * This codec reads/writes squeleporter data to/from disk
     */
    public static final Codec<SqueleporterData> DISK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("squidData").forGetter(SqueleporterData::squidData)
            ).apply(instance, SqueleporterData::new));

    /**
     * This codec does nothing; we don't want squeleporter data synced with clients
     */
    public static final StreamCodec<ByteBuf, SqueleporterData> NETWORK_CODEC = StreamCodec.unit(new SqueleporterData(null));
}
