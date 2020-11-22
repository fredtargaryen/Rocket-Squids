package com.fredtargaryen.rocketsquids.client.gui;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

public class ConchScreen extends Screen {
    private byte conchStage;
    private double x;
    private double y;
    private double z;

    protected static final ResourceLocation NOTE = new ResourceLocation(DataReference.MODID+":textures/gui/note.png");

    private static final String[] buttonNames = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    public ConchScreen(byte conchStage) {
        super(new StringTextComponent(""));
        this.conchStage = conchStage;
        PlayerEntity ep = Minecraft.getInstance().player;
        Vector3d vec = ep.getPositionVec();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void init() {
        int minx = this.width / 2 - 144;
        int column;
        int bottomY = this.height / 2 + 100;
        switch (this.conchStage) {
            case 1:
                //The white notes of the middle octave
                for (int i = 12; i < 24; ++i) {
                    if (i != 13 && i != 15 && i != 18 && i != 20 && i != 22) {
                        column = i % 12;
                        this.addButton(new ConchButton(i,
                                minx + 24 * column,
                                bottomY - 5 * i,
                                buttonNames[column]));
                    }
                }
                break;
            default:
                //All notes of the middle 3 octaves
                for (int i = 0; i < 36; ++i) {
                    column = i % 12;
                    this.addButton(new ConchButton(i,
                            minx + 24 * column,
                            bottomY - 5 * i,
                            buttonNames[column]));
                }
                break;
        }
    }

    private class ConchButton extends Button {
        private int id;
        private Minecraft mc;
        
        public ConchButton(int buttonId, int x, int y, String buttonText) {
            super(x, y, 20, 20, new StringTextComponent(buttonText), (button) -> {});
            this.id = buttonId;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void playDownSound(SoundHandler soundHandlerIn) {
            soundHandlerIn.play(SimpleSound.master(Sounds.CONCH_NOTES[this.id], 1.0F));
            MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer((byte) this.id, ConchScreen.this.x, ConchScreen.this.y, ConchScreen.this.z));
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            FontRenderer fontrenderer = mc.fontRenderer;
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            float hoverColour = this.isHovered ? 0.1F : 0.0F;
            float red = 0.9F + hoverColour;
            float green = 0.9F * this.id / 36.0F;
            this.drawNote(stack, this.x, this.y, red, green, hoverColour);
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

            this.drawCenteredString(stack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }

        private void drawNote(MatrixStack stack, int x, int y, float red, float green, float blue) {
            mc.getTextureManager().bindTexture(NOTE);
            RenderSystem.color4f(red, green, blue, 1f);
            //Draw the quaver; see gui/note.png
            //Parameters are: top-left x; top-left y; top-left u, top-left v, width, height, texture width, texture height (will repeat if texture dimensions are smaller than region dimensions)
            AbstractGui.blit(stack, x, y - 48, 0, 0, 36, 68, 36, 68);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
