// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.stream.Stream;

public class ValueIOHelper {
    private static final HolderLookup.Provider dummyHoldersProvider = HolderLookup.Provider.create(Stream.empty());

    public static CompoundTag getValueIOAsCompoundTag(ValueIOSerializable data) {
        TagValueOutput vo = TagValueOutput.createWithoutContext(null);
        data.serialize(vo);
        return vo.buildResult();
    }

    public static ValueInput getCompoundTagAsValueInput(CompoundTag tag) {
        return TagValueInput.create(null, dummyHoldersProvider, tag);
    }
}
