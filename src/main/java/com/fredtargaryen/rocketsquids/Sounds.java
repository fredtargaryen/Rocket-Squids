package com.fredtargaryen.rocketsquids;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Sounds {
    /**
     * Declare sounds
     */
    public static SoundEvent BLASTOFF;
    public static SoundEvent[] CONCH_NOTES;

    public static void constructAndRegisterSoundEvents() {
        //Making sounds
        BLASTOFF = new SoundEvent(new ResourceLocation(DataReference.MODID, "blastoff"))
                .setRegistryName("blastoff");
        CONCH_NOTES = new SoundEvent[] {
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc3"))
                        .setRegistryName("conchc3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc#3"))
                        .setRegistryName("conchc#3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd3"))
                        .setRegistryName("conchd3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd#3"))
                        .setRegistryName("conchd#3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche3"))
                        .setRegistryName("conche3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf3"))
                        .setRegistryName("conchf3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf#3"))
                        .setRegistryName("conchf#3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg3"))
                        .setRegistryName("conchg3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg#3"))
                        .setRegistryName("conchg#3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha3"))
                        .setRegistryName("concha3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha#3"))
                        .setRegistryName("concha#3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb3"))
                        .setRegistryName("conchb3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc4"))
                        .setRegistryName("conchc4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc#4"))
                        .setRegistryName("conchc#4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd4"))
                        .setRegistryName("conchd4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd#4"))
                        .setRegistryName("conchd#4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche4"))
                        .setRegistryName("conche4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf4"))
                        .setRegistryName("conchf4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf#4"))
                        .setRegistryName("conchf#4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg4"))
                        .setRegistryName("conchg4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg#4"))
                        .setRegistryName("conchg#4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha4"))
                        .setRegistryName("concha4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha#4"))
                        .setRegistryName("concha#4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb4"))
                        .setRegistryName("conchb4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc5"))
                        .setRegistryName("conchc5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc#5"))
                        .setRegistryName("conchc#5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd5"))
                        .setRegistryName("conchd5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd#5"))
                        .setRegistryName("conchd#5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche5"))
                        .setRegistryName("conche5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf5"))
                        .setRegistryName("conchf5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf#5"))
                        .setRegistryName("conchf#5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg5"))
                        .setRegistryName("conchg5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg#5"))
                        .setRegistryName("conchg#5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha5"))
                        .setRegistryName("concha5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha#5"))
                        .setRegistryName("concha#5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb5"))
                        .setRegistryName("conchb5"),
        };

        //Registering sounds
        ForgeRegistries.SOUND_EVENTS.register(BLASTOFF);
        for(SoundEvent se : CONCH_NOTES) {
            ForgeRegistries.SOUND_EVENTS.register(se);
        }
    }
}
