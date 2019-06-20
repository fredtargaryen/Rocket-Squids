package com.fredtargaryen.rocketsquids.client.gui;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.Sounds;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

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
        this.x = ep.posX;
        this.y = ep.posY;
        this.z = ep.posZ;
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
            super(x, y, 20, 20, buttonText, (button) -> {});
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
        public void render(int mouseX, int mouseY, float partialTicks) {
            FontRenderer fontrenderer = mc.fontRenderer;
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            float hoverColour = this.isHovered ? 0.1F : 0.0F;
            float red = 0.9F + hoverColour;
            float green = 0.9F * this.id / 36.0F;
            this.drawNote(this.x, this.y, red, green, hoverColour);
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

            this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }

        private void drawNote(int x, int y, float red, float green, float blue) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bb = tessellator.getBuffer();
            mc.getTextureManager().bindTexture(NOTE);
            //Draw the quaver; see gui/note.png
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            bb.pos(x + 36, y + 20, 0.0).tex(1.0, 1.0).color(red, green, blue, 1.0F).endVertex();
            bb.pos(x + 36, y - 48, 0.0).tex(1.0, 0.0).color(red, green, blue, 1.0F).endVertex();
            bb.pos(x        , y - 48, 0.0).tex(0.0, 0.0).color(red, green, blue, 1.0F).endVertex();
            bb.pos(x        , y + 20, 0.0).tex(0.0, 1.0).color(red, green, blue, 1.0F).endVertex();
            tessellator.draw();
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
