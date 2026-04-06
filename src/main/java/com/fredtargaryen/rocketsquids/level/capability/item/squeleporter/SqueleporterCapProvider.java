// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.capability.item.squeleporter;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqueleporterCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<SqueleporterCap> SQUELEPORTER_CAP = CapabilityManager.get(new CapabilityToken<SqueleporterCap>() { });

    private SqueleporterCap squeleporterCap = null;
    private final LazyOptional<SqueleporterCap> optional = LazyOptional.of(this::createSqueleporterCap);

    private SqueleporterCap createSqueleporterCap() {
        if (this.squeleporterCap == null) {
            this.squeleporterCap = new SqueleporterCap();
        }

        return this.squeleporterCap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == SQUELEPORTER_CAP) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createSqueleporterCap().saveNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createSqueleporterCap().loadNBT(nbt);
    }
}
