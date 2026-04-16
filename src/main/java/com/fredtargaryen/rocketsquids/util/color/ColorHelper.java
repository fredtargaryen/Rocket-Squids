// Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
// This code was taken and adapted from https://github.com/Beethoven92/BetterEndForge
// and remains under the MIT license, all credit goes to them
package com.fredtargaryen.rocketsquids.util.color;

import 	net.minecraft.util.Mth;

public class ColorHelper {
    private static final int ALPHA = 255 << 24;

    /**
     * Converts RGB color to Decimal color with no safe guards, for safe guards use getColor(r, g, b)
     * @param r red
     * @param g green
     * @param b blue
     * @return supplied RGB color in Decimal
     */
    public static int color(int r, int g, int b)
    {
        return ALPHA | (r << 16) | (g << 8) | b;
    }

    /**
     * Clamps and Converts RGB color to Decimal color
     * @param r red (0 - 255)
     * @param g green (0 - 255)
     * @param b blue (0 - 255)
     * @return supplied RGB color in Decimal
     */
    public static int getColor(int r, int g, int b)
    {
        r = Mth.clamp(r, 0, 255);
        g = Mth.clamp(g, 0, 255);
        b = Mth.clamp(b, 0, 255);
        return color(r, g, b);
    }
}
