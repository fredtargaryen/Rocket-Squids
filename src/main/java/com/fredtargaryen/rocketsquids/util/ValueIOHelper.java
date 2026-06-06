// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.util;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.stream.Stream;

public class ValueIOHelper {
    private static final HolderLookup.Provider dummyHoldersProvider = HolderLookup.Provider.create(Stream.empty());

    public record ValueIOHelperPathElement(String name) implements ProblemReporter.PathElement {
        @Override
        public String get() {
            return this.name();
        }
    }

    private static ProblemReporter.ScopedCollector createScopedCollector() {
        return new ProblemReporter.ScopedCollector(new ValueIOHelperPathElement(DataReference.MODID), RocketSquidsBase.LOGGER);
    }

    public static CompoundTag getValueIOAsCompoundTag(ValueIOSerializable data) {
        try (ProblemReporter.ScopedCollector problems = createScopedCollector()) {
            TagValueOutput vo = TagValueOutput.createWithoutContext(problems);
            data.serialize(vo);
            return vo.buildResult();
        }
    }

    public static ValueInput getCompoundTagAsValueInput(CompoundTag tag) {
        try (ProblemReporter.ScopedCollector problems = createScopedCollector()) {
            return TagValueInput.create(problems, dummyHoldersProvider, tag);
        }
    }

    public static TagValueOutput getNewEmptyCompoundTagAsValueOutput() {
        try (ProblemReporter.ScopedCollector problems = createScopedCollector()) {
            return TagValueOutput.createWithoutContext(problems);
        }
    }
}
