package org.xpen.kingsoft.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class AsfHandler implements FileTypeHandler {
    
    public static final byte[] MAGIC_ASF = {0x41, 0x53, 0x46, 0x20}; //ASF
    public static final byte[] MAGIC_100 = {0x31, 0x2E, 0x30, 0x30}; //1.00
    public static final byte[] MAGIC_101 = {0x31, 0x2E, 0x30, 0x31}; //1.01
    
    private static final Logger LOG = LoggerFactory.getLogger(AsfHandler.class);
    
    private String datFileName;
    private String fname;
//    private String extension;
//    private boolean keepOldFileName;
    private byte[] bytes;
    ByteBuffer buffer;
//    private Color[] colors;
//    private int width;
//    private int height;
//    private BufferedImage bi;
    
    private Header header;
    public Color[] colors;
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    
    BufferedImage bi;

    public AsfHandler() {
    }

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;
        LOG.debug("STARTING fname={}", fname);
        
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        clear();
        decodeHeader();
        decodePallete();
        decodeFat();
        decodePixel();
    }

    private void clear() {
        colors = null;
        fatEntries.clear();
    }

    private void decodeHeader() throws Exception {
        byte[] magic = new byte[4];
        buffer.get(magic);
        if (!Arrays.equals(magic, MAGIC_ASF)) {
            throw new RuntimeException("bad magic");
        }
        
        byte[] version = new byte[4];
        buffer.get(version);
        if ((!Arrays.equals(version, MAGIC_100)) && (!Arrays.equals(version, MAGIC_101))) {
            throw new RuntimeException("bad version");
        }
        
        buffer.position(0x10);
        
        header = new Header();
        header.decode(buffer);
        
    }

    private void decodePallete() throws Exception {
        buffer.position(0x40);
        colors = new Color[header.colorCount];
        
        for (int i = 0; i < colors.length; i++) {
            int b =  buffer.get() & 0xFF;
            int g =  buffer.get() & 0xFF;
            int r =  buffer.get() & 0xFF;
            int a =  buffer.get() & 0xFF;
            a =  0xFF;
            colors[i] = new Color(r,g,b,a);
        }
        
    }

    private void decodeFat() throws Exception {
        LOG.debug("buffer.position()={}", buffer.position());
        for (int i = 0; i < header.frameCount; i++) {
            
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            //fatEntry.datFileName = this.fileName;
            fatEntries.add(fatEntry);
        }
    }

    private void decodePixel() throws Exception {
        for (int k = 0; k < fatEntries.size(); k++) {
            FatEntry fatEntry = fatEntries.get(k);
            //raf.seek(fatEntry.offset);
            buffer.position(fatEntry.offset);
            
            int width = header.width;
            int height = header.height;
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            //ASF图片的特点就是每个点都能定义透明度
            //1D 00 01 2C 70
            //其中1D 00表示接下来1D个像素，透明度为0
            //01 2C 70表示接下来的01个像素透明度是2C，这个像素的颜色是70
            
            //04 FF 70 33 29 25
            //表示接下来04个像素透明度是FF（即完全不透明），这四个像素的颜色数据是70 33 29 25
            int pixelPos = 0;
            while (pixelPos < width * height) {
                int repeatCount = buffer.get() & 0xFF;
                int alphaValue = buffer.get() & 0xFF;
                
                if (alphaValue == 0) {
                    for (int i = 0; i < repeatCount; i++) {
                        bi.setRGB(pixelPos % width, pixelPos / width, 0); //transparent
                        pixelPos++;
                    }
                } else {
                    for (int i = 0; i < repeatCount; i++) {
                        int colorIndex = buffer.get() & 0xFF;
                        int value = colors[colorIndex].getRGB() | (alphaValue << 24); //semi transparent
                        bi.setRGB(pixelPos % width, pixelPos / width, value);
                        pixelPos++;
                    }
                }
            }
            
            //LOG.debug("writing file, fname={}, k={}", fname, k);
            writeFile(k);
            
        }
        
    }

    private void writeFile(int index) throws Exception {
        File outFile = null;
        String fourDigit = StringUtils.leftPad(String.valueOf(index + 1), 4, '0');
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/asf/" + fname + "_" + fourDigit + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

    public class Header {
        public int width;
        public int height;
        public int frameCount;
        public int direction;
        public int colorCount;
        public int interval;  //interval(ms/frame)
        public int left;
        public int bottom;

        public void decode(ByteBuffer buffer) {
            width = buffer.getInt();
            height = buffer.getInt();
            frameCount = buffer.getInt();
            direction = buffer.getInt();
            colorCount = buffer.getInt();
            interval = buffer.getInt();
            left = buffer.getInt();
            bottom = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public class FatEntry {
        public String datFileName;
        public String fname;
        public int offset;
        public int size;

        public void decode(ByteBuffer buffer) {
            offset = buffer.getInt();
            size = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
