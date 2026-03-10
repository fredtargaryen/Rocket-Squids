package com.fredtargaryen.rocketsquids.client.gui;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessagePlayNoteServer;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

public class ConchNumberButton extends ExtendedButton {
    private final int id;
    private final Minecraft mc;

    private final ConchScreen screen;

    private static final Component questionMark = Component.literal("?");

    @SuppressWarnings("removal")
    private static final ResourceLocation NUMBER = new ResourceLocation(DataReference.MODID, "textures/gui/numbernote.png");

    public ConchNumberButton(int buttonId, int x, int y, String buttonText, ConchScreen screen) {
        super(x, y, 32, 32, Component.literal(buttonText), (button) -> {
            if(screen.changingNumberNote == -1) {
                screen.changingNumberNote = buttonId;
                button.setMessage(questionMark);
            }
        });
        this.screen = screen;
        this.id = buttonId;
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundHandlerIn) {
        int noteId = screen.notes[this.id];
        if(noteId > -1 && screen.playingNotes[noteId] <= 0f) {
            soundHandlerIn.play(SimpleSoundInstance.forUI(ModSounds.CONCH_NOTES[noteId], 1.0F));
            MessageHandler.INSTANCE.sendToServer(new MessagePlayNoteServer((byte) noteId, screen.getX(), screen.getY(), screen.getZ()));
            screen.playingNotes[noteId] = 10f;
        }
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        Font fontrenderer = mc.font;
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        float red, green, blue;
        int noteId = screen.notes[this.id];
        if (noteId == -1) {
            //No note assigned to this button. Make it sort of grey
            red = green = blue = 0.6f + (this.isHovered ? 0.1f : 0f);
        } else {
            if (screen.playingNotes[noteId] > 0f) {
                //This note is on a "cooldown"; make it dark gray to symbolise that
                red = green = blue = 0.1f;
            } else {
                //Make the button a colour equal to that of the selected note.
                //Add 0.1 to each component of the colour, to increase the brightness slightly, if hovered over.
                blue = this.isHovered ? 0.1f : 0f;
                red = 0.9F + blue;
                green = 0.9F * noteId / 36.0F + blue;
            }
        }

        this.drawButton(stack, this.getX(), this.getY(), red, green, blue);
        int j = ColorHelper.getColor(224, 224, 224);

        if (packedFGColor != 0) {
            j = packedFGColor;
        } else if (!this.active) {
            j = ColorHelper.getColor(160, 160, 160);
        } else if (this.isHovered) {
            j = ColorHelper.getColor(255, 255, 160);
        }

        drawCenteredString(stack, fontrenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j);
        drawCenteredString(stack, fontrenderer,
                Component.literal("" + (this.id == 9 ? 0: this.id + 1)),
                this.getX() + this.width / 2, this.getY() + 34, j);
    }

    private void drawButton(PoseStack stack, int x, int y, float red, float green, float blue) {
        RenderSystem.setShaderColor(red, green, blue, 1f);
        this.renderTexture(stack, NUMBER, x, y, 0, 0, 0, 32, 32, 32, 32);
    }
}
