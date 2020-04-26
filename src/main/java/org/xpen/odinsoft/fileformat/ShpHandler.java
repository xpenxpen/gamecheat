package org.xpen.odinsoft.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class ShpHandler implements FileTypeHandler {
    public static final int MAGIC_TLHS = 0x53484C54; //TLHS
    
    private ByteBuffer buffer;
    private BufferedImage bi;
    private String datFileName;
    private String fname;
    int width;
    int height;

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;

        buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        
        clear();
        decodeHeader();
        getPixel();

    }

    private void clear() {
    }

    private void decodeHeader() throws Exception {
        
        int magic = buffer.getInt();
        if (magic != MAGIC_TLHS) {
           throw new RuntimeException("bad magic");
        }
        
        buffer.position(0x14);
        width = buffer.getInt();
        height = buffer.getInt();
        buffer.getInt(); //offsetx
        buffer.getInt(); //offsety
    }

    private void getPixel() throws Exception {
        
        int[] eachLineOffset = new int[height + 1];
        for (int i = 0; i < height; i++) {
            eachLineOffset[i] = buffer.getInt();
        }
        eachLineOffset[height] = buffer.capacity();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        //rgb565
        /*
        1:00 00 03 00 04 21 04 21 04 21 FF FF
        2:00 00 04 00 04 21 7D EF 7D EF 04 21 FF FF
        3:00 00 05 00 04 21 7D EF 7D EF 7D EF 04 21 FF FF
        4:00 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        5:01 00 05 00 04 21 38 C6 7D EF 7D EF 04 21 FF FF
        6:01 00 05 00 04 21 75 AD BA D6 7D EF 04 21 FF FF
        7:02 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        8:02 00 05 00 04 21 75 AD FB DE 7D EF 04 21 FF FF
        9:03 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        */
        for (int j = 0; j < height; j++) {
            //System.out.println("starting line " + j + ", raf.pos=" + raf.getFilePointer());
            if (buffer.position() != eachLineOffset[j]) {
                throw new RuntimeException("Unexpected SHP format(eachLineOffset not align)");
            }
            
            int pixelPos = 0;
            while (eachLineOffset[j + 1] - buffer.position() > 2) {
                int repeatCount = buffer.getShort();
                //System.out.println(repeatCount + " number of transparent color");
                for (int i = pixelPos; i < repeatCount; i++) {
                    bi.setRGB(pixelPos, j, 0); //transparent
                    pixelPos++;
                }
                
                repeatCount = buffer.getShort();
                //System.out.println(repeatCount + " number of untransparent color");
                for (int i = 0; i < repeatCount; i++) {
                    int rgb555 = buffer.getShort() & 0xFFFF;
                    Color color = ColorUtil.rgb565ToRgb888(rgb555);
                    bi.setRGB(pixelPos, j, color.getRGB());
                    pixelPos++;
                }
            }
            
            //final 2 bytes must be FFFF
            int final2Bytes = buffer.getShort() & 0xFFFF;
            if (final2Bytes != 0xFFFF) {
                throw new RuntimeException("Unexpected SHP format(not FFFF at line end)");
            }
            
            for (int i = pixelPos; i < width; i++) {
                bi.setRGB(pixelPos, j, 0); //transparent
                pixelPos++;
            }
        }
        
        String baseFileName = FilenameUtils.removeExtension(fname);
        File outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + baseFileName + ".png");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

}
