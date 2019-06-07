package com.fredtargaryen.rocketsquids;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.lwjgl.system.CallbackI;

public class Sounds {
    //////////////////
    //Declare sounds//
    //////////////////
    public static SoundEvent BLASTOFF;
    public static SoundEvent SQUIDTP_IN;
    public static SoundEvent SQUIDTP_OUT;
    public static SoundEvent[] CONCH_NOTES;

    public static void constructAndRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        BLASTOFF = new SoundEvent(new ResourceLocation(DataReference.MODID, "blastoff"))
                .setRegistryName("blastoff");
        SQUIDTP_IN = new SoundEvent(new ResourceLocation(DataReference.MODID, "tpin"))
                .setRegistryName("tpin");
        SQUIDTP_OUT = new SoundEvent(new ResourceLocation(DataReference.MODID, "tpout"))
                .setRegistryName("tpout");
        CONCH_NOTES = new SoundEvent[]{
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc3"))
                        .setRegistryName("conchc3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchcs3"))
                        .setRegistryName("conchcs3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd3"))
                        .setRegistryName("conchd3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchds3"))
                        .setRegistryName("conchds3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche3"))
                        .setRegistryName("conche3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf3"))
                        .setRegistryName("conchf3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchfs3"))
                        .setRegistryName("conchfs3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg3"))
                        .setRegistryName("conchg3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchgs3"))
                        .setRegistryName("conchgs3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha3"))
                        .setRegistryName("concha3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchas3"))
                        .setRegistryName("conchas3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb3"))
                        .setRegistryName("conchb3"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc4"))
                        .setRegistryName("conchc4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchcs4"))
                        .setRegistryName("conchcs4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd4"))
                        .setRegistryName("conchd4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchds4"))
                        .setRegistryName("conchds4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche4"))
                        .setRegistryName("conche4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf4"))
                        .setRegistryName("conchf4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchfs4"))
                        .setRegistryName("conchfs4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg4"))
                        .setRegistryName("conchg4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchgs4"))
                        .setRegistryName("conchgs4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha4"))
                        .setRegistryName("concha4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchas4"))
                        .setRegistryName("conchas4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb4"))
                        .setRegistryName("conchb4"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchc5"))
                        .setRegistryName("conchc5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchcs5"))
                        .setRegistryName("conchcs5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchd5"))
                        .setRegistryName("conchd5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchds5"))
                        .setRegistryName("conchds5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conche5"))
                        .setRegistryName("conche5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchf5"))
                        .setRegistryName("conchf5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchfs5"))
                        .setRegistryName("conchfs5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchg5"))
                        .setRegistryName("conchg5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchgs5"))
                        .setRegistryName("conchgs5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "concha5"))
                        .setRegistryName("concha5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchas5"))
                        .setRegistryName("conchas5"),
                new SoundEvent(new ResourceLocation(DataReference.MODID, "conchb5"))
                        .setRegistryName("conchb5")
        };
        IForgeRegistry<SoundEvent> reg = event.getRegistry();
        reg.registerAll(BLASTOFF, SQUIDTP_IN, SQUIDTP_OUT);
        for(SoundEvent soundEvent : CONCH_NOTES) {
            reg.register(soundEvent);
        }
    }
}
