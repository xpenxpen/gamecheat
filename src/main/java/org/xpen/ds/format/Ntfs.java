package org.xpen.ds.format;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ntfs {
    
    private static final Logger LOG = LoggerFactory.getLogger(Ntfs.class);
    
    /**
     * NTFS(Nintendo Tile Format Screen) has 2 bytes(16 bits)
     *  |--- PPPP X Y NNNNNNNNNN
     *    |---  PPPP => Palette number (Usually it is 0000)
     *    |---    X  => X flip (boolean)
     *    |---    Y  => Y flip (boolean)
     *    |---  NNNNNNNNNN => Tile number
     */
    public static BufferedImage getPixel(ByteBuffer buffer, int width, int height, int[][] tiles, Color[][] colors) throws Exception {
        int xTileCount = width / 8;
        int yTileCount = height / 8;
        int[][] maps = new int[yTileCount][xTileCount];
        //This is the real image size
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < yTileCount; y++) {
            for (int x = 0; x < xTileCount; x++) {
                int bits = buffer.getShort() & 0xFFFF;
                int paletteIndex = (bits & 0xF000) >> 12;
                int xFlip = (bits & 0x800) >> 11;
                int yFlip = (bits & 0x400) >> 10;
                //System.out.println(paletteIndex);
                int tileIndex = bits & 0x3FF;
                //System.out.println(tileIndex);
                //if (tileIndex > 142) {
                //    tileIndex = 0;
                //}
                if (tileIndex >= tiles.length) {
                    tileIndex = tileIndex % tiles.length;
                }
                int[] tile = tiles[tileIndex];
                Color[] pickPalette = colors[paletteIndex];
                for (int k = 0; k < 64; k++) {
                    //System.out.println(tile[k]);
                    if (tile[k] > pickPalette.length) {
                        LOG.debug("Warning: tile out of index " + tile[k]);
                        //tile[k] = colors.length - 1;
                        tile[k] = tile[k] % colors.length;
                    }
                    bi.setRGB(x * 8 + k % 8, y * 8 + k / 8, pickPalette[tile[k]].getRGB());
                }
           }
        }
        return bi;
    }

}
