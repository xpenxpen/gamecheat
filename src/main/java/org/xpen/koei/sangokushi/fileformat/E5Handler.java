package org.xpen.koei.sangokushi.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class E5Handler implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(E5Handler.class);
    
    private String datFileName;
    private String fname;
    private byte[] bytes;
    ByteBuffer buffer;
    
    public static Color[] palletes;
    int width;
    int height;
    BufferedImage bi;

    public E5Handler() {
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
        if (this.datFileName.equals("Effarea")) {
            width = 64;
            height = 64;
        } else if (this.datFileName.equals("Face")) {
            width = 64;
            height = 80;
        }  else if (this.datFileName.equals("Gate")) {
            width = 144;
            height = 144;
        }  else if (this.datFileName.equals("Hitarea")) {
            width = 64;
            height = 64;
        }
    }

    private void decodePixel() throws Exception {
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int colorIndex = buffer.get() & 0xFF;
                    bi.setRGB(i, j, palletes[colorIndex].getRGB());
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
