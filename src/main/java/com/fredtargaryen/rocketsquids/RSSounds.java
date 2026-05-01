// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

@SuppressWarnings("removal")
public class RSSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLASTOFF = registerSoundEvents("blastoff");

    public static final DeferredHolder<SoundEvent, SoundEvent> SQUIDTP_IN = registerSoundEvents("tpin");
    public static final DeferredHolder<SoundEvent, SoundEvent> SQUIDTP_OUT = registerSoundEvents("tpout");

    public static SoundEvent[] CONCH_NOTES = new SoundEvent[] {
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchc3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchcs3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchd3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchds3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conche3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchf3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchfs3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchg3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchgs3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "concha3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchas3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchb3")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchc4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchcs4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchd4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchds4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conche4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchf4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchfs4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchg4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchgs4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "concha4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchas4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchb4")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchc5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchcs5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchd5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchds5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conche5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchf5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchfs5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchg5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchgs5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "concha5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchas5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "conchb5")),
            SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "recognition"))
    };

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        for(SoundEvent soundEvent : CONCH_NOTES) {
            SOUND_EVENTS.register(soundEvent.getLocation().getPath(), () -> soundEvent);
        }
        SOUND_EVENTS.register(eventBus);
    }
}
