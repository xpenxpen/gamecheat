package org.xpen.capcom.aceattorney.gk1.fileformat;

import java.awt.image.BufferedImage;

public class ImgLongFace extends Img2 {
    
    @Override
    public void getHeight() {
        height = 136;
        tileCount = outBytes.length / 32;
    }

    @Override
    public void drawImg(int width, int height, BufferedImage bi) {
        int tileIndex = 0;
        
        //自己拼图
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 32; x++) {
                tileIndex = 32 + 32 * y + (x / 8) * 8 + x % 8;
                setRgb(tileIndex, y * 8 + x % 8, x / 8, bi);
            }
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 8; x++) {
                tileIndex = 8 * y + x % 8;
                setRgb(tileIndex, x + y * 8, 4, bi);
            }
        }
        
    }

    private void setRgb(int tileIndex, int x, int y, BufferedImage bi) {
        for (int k = 0; k < 64; k++) {
            bi.setRGB(x * 8 + k % 8, y * 8 + k / 8, palette[tiles[tileIndex][k]].getRGB());
        }
    }

}
