package com.fredtargaryen.rocketsquids.cap.entity.baby;

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

public class BabyCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<BabyCap> BABYCAP = CapabilityManager.get(new CapabilityToken<BabyCap>() { });

    private BabyCap babyCap = null;
    private final LazyOptional<BabyCap> optional = LazyOptional.of(this::createBabyCap);

    private BabyCap createBabyCap() {
        if (this.babyCap == null) {
            this.babyCap = new BabyCap();
        }

        return this.babyCap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == BABYCAP) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createBabyCap().saveNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createBabyCap().loadNBT(nbt);
    }
}
