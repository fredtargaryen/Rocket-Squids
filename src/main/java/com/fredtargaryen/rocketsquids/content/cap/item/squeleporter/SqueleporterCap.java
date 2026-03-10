package com.fredtargaryen.rocketsquids.content.cap.item.squeleporter;

import net.minecraft.nbt.CompoundTag;

public class SqueleporterCap {
    private CompoundTag squidData;
    private CompoundTag squidCapabilityData;

    public SqueleporterCap() {

    }

    public CompoundTag getSquidData() {
        return this.squidData;
    }

    public void setSquidData(CompoundTag nbt) {
        this.squidData = nbt;
    }

    public CompoundTag getSquidCapabilityData() {
        return this.squidCapabilityData;
    }

    public void setSquidCapabilityData(CompoundTag nbt) {
        this.squidCapabilityData = nbt;
    }

    public CompoundTag saveNBT(CompoundTag comp) {
        CompoundTag normalSquidData = this.getSquidData();
        CompoundTag capSquidData = this.getSquidCapabilityData();
        comp.put("normal", normalSquidData == null ? new CompoundTag() : normalSquidData);
        comp.put("capability", capSquidData == null ? new CompoundTag() : this.getSquidCapabilityData());
        return comp;
    }

    public CompoundTag loadNBT(CompoundTag comp) {
        this.setSquidData(comp.getCompound("normal"));
        this.setSquidCapabilityData(comp.getCompound("capability"));
        return comp;
    }
}
