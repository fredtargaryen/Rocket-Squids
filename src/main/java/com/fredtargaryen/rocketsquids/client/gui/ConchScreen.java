package com.fredtargaryen.rocketsquids.client.gui;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConchScreen extends Screen {
    private final byte conchStage;
    private final double x;
    private final double y;
    private final double z;

    private int changingNumberNote;

    private static final ResourceLocation NOTE = new ResourceLocation(DataReference.MODID+":textures/gui/note.png");
    private static final ResourceLocation NUMBER = new ResourceLocation(DataReference.MODID+":textures/gui/numbernote.png");

    private static final Component[] buttonNames = {
            new TextComponent("C"),
            new TextComponent("C#"),
            new TextComponent("D"),
            new TextComponent("D#"),
            new TextComponent("E"),
            new TextComponent("F"),
            new TextComponent("F#"),
            new TextComponent("G"),
            new TextComponent("G#"),
            new TextComponent("A"),
            new TextComponent("A#"),
            new TextComponent("B")
    };

    private static final Component questionMark = new TextComponent("?");

    private final int[] notes = new int[10];
    private final List<ConchNumberButton> conchNumberButtons;

    private final float[] playingNotes = new float[36];

    public ConchScreen(byte conchStage) {
        super(TextComponent.EMPTY);
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
                                buttonNames[column].getString(),
                                this));
                    }
                }
                break;
            case 3:
                //The number buttons
                for(int i = 0; i < 10; i++) {
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
                            buttonNames[column].getString(),
                            this));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        for(int i = 0; i < this.playingNotes.length; i++)
        {
            this.playingNotes[i] -= partialTicks;
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
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

    private class ConchButton extends Button {
        private final int id;
        private final Minecraft mc;
        
        public ConchButton(int buttonId, int x, int y, String buttonText, ConchScreen screen) {
            super(x, y, 20, 20, new TextComponent(buttonText), (button) -> {
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
                soundHandlerIn.play(SimpleSoundInstance.forUI(Sounds.CONCH_NOTES[this.id], 1.0F));
                MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer((byte) this.id, ConchScreen.this.x, ConchScreen.this.y, ConchScreen.this.z));
                ConchScreen.this.playingNotes[this.id] = 10f;
            }
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            Font fontrenderer = mc.font;
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            float red, green, blue;
            if(ConchScreen.this.playingNotes[this.id] > 0f)
            {
                //This note is on a "cooldown"; make it dark gray to symbolise that
                red = green = blue = 0.1f;
            }
            else {
                blue = this.isHovered ? 0.1F : 0.0F;
                red = 0.9F + blue;
                green = 0.9F * this.id / 36.0F + blue;
            }
            this.drawNote(stack, this.x, this.y, red, green, blue);
            int j = 14737632;

            if (packedFGColor != 0) {
                j = packedFGColor;
            }
            else if (!this.active) {
                j = 10526880;
            }
            else if (this.isHovered) {
                j = 16777120;
            }

            drawCenteredString(stack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }

        private void drawNote(PoseStack stack, int x, int y, float red, float green, float blue) {
            mc.getTextureManager().bindForSetup(NOTE);
            RenderSystem.setShaderColor(red, green, blue, 1f);
            //Draw the quaver; see gui/note.png
            //Parameters are: top-left x; top-left y; top-left u, top-left v, width, height, texture width, texture height (will repeat if texture dimensions are smaller than region dimensions)
            GuiComponent.blit(stack, x + 2, y - 37, 0, 0, 27, 54, 27, 54);
        }
    }

    private class ConchNumberButton extends Button {
        private final int id;
        private final Minecraft mc;

        public ConchNumberButton(int buttonId, int x, int y, String buttonText, ConchScreen screen) {
            super(x, y, 32, 32, new TextComponent(buttonText), (button) -> {
                if(screen.changingNumberNote == -1) {
                    screen.changingNumberNote = buttonId;
                    button.setMessage(questionMark);
                }
            });
            this.id = buttonId;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void playDownSound(@NotNull SoundManager soundHandlerIn) {
            int noteId = ConchScreen.this.notes[this.id];
            if(noteId > -1 && ConchScreen.this.playingNotes[noteId] <= 0f) {
                soundHandlerIn.play(SimpleSoundInstance.forUI(Sounds.CONCH_NOTES[noteId], 1.0F));
                MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer((byte) noteId, ConchScreen.this.x, ConchScreen.this.y, ConchScreen.this.z));
                ConchScreen.this.playingNotes[noteId] = 10f;
            }
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            Font fontrenderer = mc.font;
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            float red, green, blue;
            int noteId = ConchScreen.this.notes[this.id];
            if(noteId == -1) {
                //No note assigned to this button. Make it sort of grey
                red = green = blue = 0.6f + (this.isHovered ? 0.1f : 0f);
            }
            else {
                if(ConchScreen.this.playingNotes[noteId] > 0f)
                {
                    //This note is on a "cooldown"; make it dark gray to symbolise that
                    red = green = blue = 0.1f;
                }
                else {
                    //Make the button a colour equal to that of the selected note.
                    //Add 0.1 to each component of the colour, to increase the brightness slightly, if hovered over.
                    blue = this.isHovered ? 0.1f : 0f;
                    red = 0.9F + blue;
                    green = 0.9F * noteId / 36.0F + blue;
                }
            }

            this.drawButton(stack, this.x, this.y, red, green, blue);
            int j = 14737632;

            if (packedFGColor != 0) {
                j = packedFGColor;
            }
            else if (!this.active) {
                j = 10526880;
            }
            else if (this.isHovered) {
                j = 16777120;
            }

            drawCenteredString(stack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            drawCenteredString(stack, fontrenderer,
                    new TextComponent("" + (this.id == 9 ? 0: this.id + 1)),
                    this.x + this.width / 2, this.y + 34, j);
        }

        private void drawButton(PoseStack stack, int x, int y, float red, float green, float blue) {
            mc.getTextureManager().bindForSetup(NUMBER);
            RenderSystem.setShaderColor(red, green, blue, 1f);
            //Draw the button; see gui/numbernote.png
            //Parameters are: top-left x; top-left y; top-left u, top-left v, width, height, texture width, texture height (will repeat if texture dimensions are smaller than region dimensions)
            GuiComponent.blit(stack, x, y, 0, 0, 32, 32, 32, 32);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
