package com.fredtargaryen.rocketsquids.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiConch extends GuiScreen {
    private byte conchStage;
    private static final String[] buttonNames = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    public GuiConch(byte conchStage) {
        this.conchStage = conchStage;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        int quarterWidth = this.width / 5;
        int column;
        int bottomY = this.height - 60;
        switch (this.conchStage) {
            case 1:
                //The white notes of the middle octave
                for (int i = 12; i < 24; ++i) {
                    if (i != 13 && i != 15 && i != 18 && i != 20 && i != 22) {
                        column = i % 12;
                        this.addButton(new ConchButton(i,
                                quarterWidth + 24 * column,
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
                            quarterWidth + 24 * column,
                            bottomY - 5 * i,
                            buttonNames[column]));
                }
                break;
        }
    }

    private class ConchButton extends GuiButton {
        public ConchButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, 20, 20, buttonText);
        }
    }
}
