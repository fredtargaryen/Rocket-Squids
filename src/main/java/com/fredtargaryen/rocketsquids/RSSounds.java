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

    public static final DeferredHolder<SoundEvent, SoundEvent> CONCH_EQUIP = registerSoundEvents("conch_equip");

    public static SoundEvent[] CONCH_NOTES = new SoundEvent[]{
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchc3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchcs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchd3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchds3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conche3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchf3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchfs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchg3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchgs3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("concha3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchas3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchb3")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchc4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchcs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchd4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchds4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conche4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchf4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchfs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchg4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchgs4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("concha4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchas4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchb4")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchc5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchcs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchd5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchds5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conche5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchf5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchfs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchg5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchgs5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("concha5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchas5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("conchb5")),
            SoundEvent.createVariableRangeEvent(DataReference.getIdentifier("recognition"))
    };

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(DataReference.getIdentifier(name)));
    }

    public static void register(IEventBus eventBus) {
        for (SoundEvent soundEvent : CONCH_NOTES) {
            SOUND_EVENTS.register(soundEvent.location().getPath(), () -> soundEvent);
        }
        SOUND_EVENTS.register(eventBus);
    }
}
