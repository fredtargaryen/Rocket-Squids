package com.fredtargaryen.rocketsquids.cap.item.squeleporter;

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
        CompoundTag nbt = createSqueleporterCap().saveNBT(new CompoundTag());;
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createSqueleporterCap().loadNBT(nbt);
    }
}
