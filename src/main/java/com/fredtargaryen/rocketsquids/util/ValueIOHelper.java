// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class ValueIOHelper {
    public static CompoundTag getValueIOAsCompoundTag(ValueIOSerializable data) {
        TagValueOutput vo = TagValueOutput.createWithoutContext(null);
        data.serialize(vo);
        return vo.buildResult();
    }

    /**
     * TODO Likely to throw its toys out of the pram if the provder is null - can I have a dummy provider to keep it happy?
     */
    public static ValueInput getCompoundTagAsValueInput(CompoundTag tag) {
        return TagValueInput.create(null, null, tag);
    }
}
