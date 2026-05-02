// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;

public class RSSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLASTOFF = registerSoundEvents("blastoff");

    public static final DeferredHolder<SoundEvent, SoundEvent> SQUIDTP_IN = registerSoundEvents("tpin");
    public static final DeferredHolder<SoundEvent, SoundEvent> SQUIDTP_OUT = registerSoundEvents("tpout");

    public static SoundEvent[] CONCH_NOTES = new SoundEvent[]{
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchc3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchcs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchd3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchds3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conche3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchf3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchfs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchg3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchgs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("concha3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchas3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchb3")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchc4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchcs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchd4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchds4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conche4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchf4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchfs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchg4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchgs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("concha4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchas4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchb4")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchc5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchcs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchd5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchds5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conche5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchf5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchfs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchg5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchgs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("concha5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchas5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("conchb5")),
            SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation("recognition"))
    };

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(DataReference.getResourceLocation(name)));
    }

    public static void register(IEventBus eventBus) {
        for (SoundEvent soundEvent : CONCH_NOTES) {
            SOUND_EVENTS.register(soundEvent.getLocation().getPath(), () -> soundEvent);
        }
        SOUND_EVENTS.register(eventBus);
    }
}
