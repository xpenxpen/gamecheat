package org.xpen.hantang.fd.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

public class DatoHandler implements FileTypeHandler {
    
    private ByteBuffer buffer;
    private BufferedImage bi;
    private String datFileName;
    private String fname;
    private byte[] bytes;
    public Color[] colors;
    public int width;
    public int height;

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;

        buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        
        getPallete();
        getPixel();

    }

    private void getPallete() throws Exception {
        InputStream is = DatoHandler.class.getClassLoader().getResourceAsStream("hantang/fd2/datoPalette.dat");
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
            int r =  is.read() * 4;
            int g =  is.read() * 4;
            int b =  is.read() * 4;
            int a =  0xFF;
            colors[i] = new Color(r,g,b,a);
        }
    }

    private void getPixel() throws Exception {
        int[] offsets = new int[5];
        for (int i = 0; i < 4; i++) {
            offsets[i] = buffer.getInt();
        }
        offsets[4] = buffer.capacity();
        
        int offsetIndex = 0;
        while (offsetIndex < 4) {
            int total = 0;
            int i = 0;
            int j = 0;
            width = buffer.getShort();
            height = buffer.getShort();
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            while (buffer.position() < offsets[offsetIndex + 1]) {
                int aNum = buffer.get() & 0xFF;
                if ((aNum & 0xC0) == 0xC0) {
                    //RLE
                    aNum = aNum & 0x3F;
                    total += aNum;
                    int colorIndex = buffer.get() & 0xFF;
                    for (int k = 0; k < aNum; k++) {
                        bi.setRGB(i, j, colors[colorIndex].getRGB());
                        i++;
                        if (i>=width) {
                            i=0;
                            j++;
                        }
                    }
                } else {
                    total += 1;
                    bi.setRGB(i, j, colors[aNum].getRGB());
                    i++;
                    if (i>=width) {
                        i=0;
                        j++;
                    }
                }
            }
            
            File outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + fname + "_" + offsetIndex + ".png");
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            ImageIO.write(bi, "PNG", outFile);
            offsetIndex++;
        }
    }

}
