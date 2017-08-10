package org.xpen.koei.sangokushi.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

public class R3Handler implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(R3Handler.class);
    
    private String datFileName;
    private String fname;
    private byte[] bytes;
    ByteBuffer buffer;
    
    public static Color[] palletes;
    int width;
    int height;
    BufferedImage bi;

    public R3Handler() {
    }

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;
        
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        getWidthHeight();
        decodePixel();
    }

    private void getWidthHeight() {
        if (this.datFileName.equals("hexbchp")) {
            width = 16;
            height = 3584;
        } else if (this.datFileName.equals("hexzchp")) {
            width = 16;
            height = 1280;
        }
    }

    private void decodePixel() throws Exception {
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i+=2) {
                    int b = (buffer.get() & 0xFF) ^ 0x55;
                    int b1 = b >>> 4;
                    int b2 = b & 0x0F;
                    bi.setRGB(i, j, palletes[b1].getRGB());
                    bi.setRGB(i+1, j, palletes[b2].getRGB());
                }
            }
            
            writeFile();
        
    }

    private void writeFile() throws Exception {
        File outFile = null;
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + fname + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

}
