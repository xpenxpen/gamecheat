package org.xpen.util;

import java.awt.Color;

public class ColorUtil {

    public static Color rgb555ToRgb888(int rgb555) {
        int b5 = rgb555 & 0x1f;
        int g5 = (rgb555 >>> 5) & 0x1f;
        int r5 = (rgb555 >>> 10) & 0x1f;
        // Scale components up to 8 bit: 
        // Shift left and fill empty bits at the end with the highest bits,
        // so 00000 is extended to 000000000 but 11111 is extended to 11111111
        int b = (b5 << 3) | (b5 >> 2);
        int g = (g5 << 3) | (g5 >> 2);
        int r = (r5 << 3) | (r5 >> 2);
        Color color = new Color(r, g, b, 255);
        return color;
    }

    public static Color rgb565ToRgb888(int rgb565) {
        int b5 = rgb565 & 0x1f;
        int g6 = (rgb565 >>> 5) & 0x3f;
        int r5 = (rgb565 >>> 11) & 0x1f;
        // Scale components up to 8 bit: 
        // Shift left and fill empty bits at the end with the highest bits,
        // so 00000 is extended to 000000000 but 11111 is extended to 11111111
        int b = (b5 << 3) | (b5 >> 2);
        int g = (g6 << 2) | (g6 >> 4);
        int r = (r5 << 3) | (r5 >> 2);
        Color color = new Color(r, g, b, 255);
        return color;
    }

}
