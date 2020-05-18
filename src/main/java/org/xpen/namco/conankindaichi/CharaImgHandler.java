package org.xpen.namco.conankindaichi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CharaImgHandler extends ImgHandler {

    private int pixelLength;
    private int xBlockCount;
    private int yBlockCount;

    public CharaImgHandler() {
        super();
    }

    /**
     * 4 0x03
     * 4 0x14
     * 4 paletteOffset
     * 4 pixelOffset
     * 4 end
     * --pos 0x20--
     * 1 x block
     * 1 y block
     */
    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //LOG.debug("newFileName={}", newFileName);
        
        buffer.position(8);
        int paletteOffset = buffer.getInt();
        int pixelOffset = buffer.getInt();
        int end = buffer.getInt();
        int paletteLength = pixelOffset - paletteOffset;
        pixelLength = end - pixelOffset;
        this.colorCount = paletteLength / 2;
        this.palette = new Color[colorCount];
        
        buffer.position(0x20);
        xBlockCount = buffer.get();
        yBlockCount = buffer.get();
        width = xBlockCount * 64;
        height = yBlockCount * 64;
                
        buffer.position(paletteOffset);
        getPallete();
        
        //getHeight();
        
        buffer.position(pixelOffset);
        getPixel();
        
        writeFile();
    }
//
//    @Override
//    protected void getHeight() {
//        if (colorCount == 16) {
//            height = pixelLength * 2 / width;
//        } else if (colorCount == 256) {
//            height = pixelLength / width;
//        } else {
//            throw new RuntimeException("Unknown colorCount:" + colorCount);
//        }
//    }

    /**
     * B1 B2
     * B3 B4
     * B5 B6
     * 
     * Width = 128
     * 
     * Each block is 64*64 pixels, consists of 64 tiles(8*8)
     * 
     * Each block:
     * t1 t2 t3 t4 t5 t6 t7 t8
     * t9 ..................t16
     * .......................
     * .....................t64
     * 
     */
    @Override
    protected void getPixel() throws Exception {

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        int xTileCount = 8;
        int yTileCount = 8;

        for (int by = 0; by < yBlockCount; by++) {
            for (int bx = 0; bx < xBlockCount; bx++) {
                
                for (int y = 0; y < yTileCount; y++) {
                    for (int x = 0; x < xTileCount; x++) {
                        
                        for (int k = 0; k < 64; k++) {
                            int bits = buffer.get() & 0xFF;
                            
                            if (colorCount == 16) {
                                int bit1 = bits & 0xF;
                                int bit2 = (bits & 0xF0) >> 4;
                                bi.setRGB(bx * 64 + x * 8 + k % 8, by * 64 + y * 8 + k / 8, palette[bit1].getRGB());
                                bi.setRGB(bx * 64 + x * 8 + k % 8, by * 64 + y * 8 + k / 8 + 1, palette[bit2].getRGB());
                                k += 1;
                            } else {
                                if (bits >= colorCount) {
                                    bits = 0;
                                }
                                
                                bi.setRGB(bx * 64 + x * 8 + k % 8, by * 64 + y * 8 + k / 8, palette[bits].getRGB());
                            }
                        }
                    }
                }
                
                
            }
        }
    }

}
