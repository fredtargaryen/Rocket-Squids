package com.fredtargaryen.rocketsquids.cap.entity.adult;

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

public class AdultCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<AdultCap> ADULTCAP = CapabilityManager.get(new CapabilityToken<AdultCap>() { });

    private AdultCap adultCap = null;
    private final LazyOptional<AdultCap> optional = LazyOptional.of(this::createAdultCap);

    private AdultCap createAdultCap() {
        if (this.adultCap == null) {
            this.adultCap = new AdultCap();
        }

        return this.adultCap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == ADULTCAP) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createAdultCap().saveNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createAdultCap().loadNBT(nbt);
    }
}
