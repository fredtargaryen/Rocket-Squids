// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.gui;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConchScreen extends Screen {
    private final byte conchStage;

    private final double x;
    public double getX() {
        return this.x;
    }
    private final double y;
    public double getY() {
        return this.y;
    }
    private final double z;
    public double getZ() {
        return this.z;
    }

    int changingNumberNote;

    @SuppressWarnings("removal")
    private static final ResourceLocation NOTE = new ResourceLocation(DataReference.MODID, "textures/gui/note.png");

    private static final Component[] buttonNames = {
            Component.literal("C").withStyle(ChatFormatting.WHITE),
            Component.literal("C#").withStyle(ChatFormatting.WHITE),
            Component.literal("D").withStyle(ChatFormatting.WHITE),
            Component.literal("D#").withStyle(ChatFormatting.WHITE),
            Component.literal("E").withStyle(ChatFormatting.WHITE),
            Component.literal("F").withStyle(ChatFormatting.WHITE),
            Component.literal("F#").withStyle(ChatFormatting.WHITE),
            Component.literal("G").withStyle(ChatFormatting.WHITE),
            Component.literal("G#").withStyle(ChatFormatting.WHITE),
            Component.literal("A").withStyle(ChatFormatting.WHITE),
            Component.literal("A#").withStyle(ChatFormatting.WHITE),
            Component.literal("B").withStyle(ChatFormatting.WHITE)
    };

    final int[] notes = new int[10];
    private final List<ConchNumberButton> conchNumberButtons;

    final float[] playingNotes = new float[36];

    public ConchScreen(byte conchStage) {
        super(Component.empty());
        this.conchStage = conchStage;
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 vec = ep.position();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.changingNumberNote = -1;
        for(int i = 0; i < 10; i++)
        {
            this.notes[i] = -1;
        }
        this.conchNumberButtons = new ArrayList<>();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void init() {
        int minx = this.width / 2 - 160;
        int column;
        int bottomNoteY = this.height / 2 + 56;
        int bottomNumberY = this.height / 2 + 76;
        switch (this.conchStage) {
            case 1:
                //The white notes of the middle octave
                for (int i = 12; i < 24; ++i) {
                    if (i != 13 && i != 15 && i != 18 && i != 20 && i != 22) {
                        column = i % 12;
                        this.addRenderableWidget(new ConchButton(i,
                                minx + 27 * column,
                                bottomNoteY - 4 * i,
                                buttonNames[column],
                                this));
                    }
                }
                break;
            case 3:
                //The number buttons
                for (int i = 0; i < 10; i++) {
                    ConchNumberButton cnb = new ConchNumberButton(i,
                            minx + 32 * i,
                            bottomNumberY,
                            "-",
                            this);
                    this.addRenderableWidget(cnb);
                    this.conchNumberButtons.add(cnb);
                }
            case 2:
                //All notes of the middle 3 octaves
                for (int i = 0; i < 36; ++i) {
                    column = i % 12;
                    this.addRenderableWidget(new ConchButton(i,
                            minx + 27 * column,
                            bottomNoteY - 4 * i,
                            buttonNames[column],
                            this));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        for(int i = 0; i < this.playingNotes.length; i++)
        {
            this.playingNotes[i] -= partialTicks;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.conchStage == (byte) 3) {
            int index = keyCode - 48; // So alpha key 0 = button 0, alpha key 1 = button 1 etc.
            if(index > -1 && index < 10) {
                ConchNumberButton cnb = this.conchNumberButtons.get(index == 0 ? 9 : index - 1);
                cnb.playDownSound(Minecraft.getInstance().getSoundManager());
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private class ConchButton extends ExtendedButton {
        private final int id;
        private final Minecraft mc;

        public ConchButton(int buttonId, int x, int y, Component buttonText, ConchScreen screen) {
            super(x, y, 20, 20, buttonText, (button) -> {
                if(screen.changingNumberNote > -1)
                {
                    screen.notes[screen.changingNumberNote] = buttonId;
                    screen.conchNumberButtons.get(screen.changingNumberNote).setMessage(
                            ConchScreen.buttonNames[buttonId % 12]
                    );
                    screen.changingNumberNote = -1;
                }
            });
            this.id = buttonId;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void playDownSound(@NotNull SoundManager soundHandlerIn) {
            if(ConchScreen.this.playingNotes[this.id] <= 0f) {
                soundHandlerIn.play(SimpleSoundInstance.forUI(ModSounds.CONCH_NOTES[this.id], 1.0F));
                MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer((byte) this.id, ConchScreen.this.x, ConchScreen.this.y, ConchScreen.this.z));
                ConchScreen.this.playingNotes[this.id] = 10f;
            }
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            Font fontrenderer = mc.font;
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            float red, green, blue;
            if (ConchScreen.this.playingNotes[this.id] > 0f)
            {
                //This note is on a "cooldown"; make it dark gray to symbolise that
                red = green = blue = 0.1f;
            } else {
                blue = this.isHovered ? 0.1F : 0.0F;
                red = 0.9F + blue;
                green = 0.9F * this.id / 36.0F + blue;
            }
            this.drawNote(guiGraphics, this.getX(), this.getY(), red, green, blue);

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.drawCenteredString(fontrenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, ColorHelper.getColor(255, 255, 255));
        }

        private void drawNote(GuiGraphics guiGraphics, int x, int y, float red, float green, float blue) {
            RenderSystem.setShaderColor(red, green, blue, 1.0F);
            this.renderTexture(guiGraphics, NOTE, x + 2, y - 37, 0, 0, 0, 27, 54, 27, 54);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
