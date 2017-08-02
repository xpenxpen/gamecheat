package org.xpen.pal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;
import org.xpen.util.ColorUtil;
import org.xpen.util.compress.LzoCompressor;



public class TswTest {
        
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width;
    int height;
    BufferedImage bi;
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private boolean isColorDepth8;
    String file = "F:/game/pal1new/run/新仙剑奇侠传/myex/All_Char/706魚0";

    
    @Test
    public void testTsw() throws Exception {
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        clear();
        getFat();
        getDat();
        close();
    }

    private void clear() {
        isColorDepth8 = false;
        fatEntries.clear();
    }

    private void getFat() throws Exception {
        fileChannel.position(6);
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(6);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatCount = buffer.getShort();
        int colorDepth = buffer.getShort(); //8
        int unknown2 = buffer.getShort(); //0x0C
        System.out.println("fatCount="+fatCount+",colorDepth="+colorDepth+",unknown2="+unknown2);
        
        if (colorDepth == 8) {
            isColorDepth8 = true;
            getPallete();
        }
        
        for (int i = 0; i < fatCount; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
              
            buffer = ByteBuffer.allocate(0x24);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(0x24);
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.decode(buffer);
        }
                
    }

    private void getPallete() throws Exception {
        colors = new Color[256];
        ByteBuffer buffer = ByteBuffer.allocate(512);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(512);
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < colors.length; i++) {
            int rgb555 = buffer.getShort() & 0xFF;
            colors[i] = ColorUtil.rgb555ToRgb888(rgb555);
        }
    }

    private void getDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            raf.seek(fatEntry.offset);
            byte[] b = new byte[fatEntry.compressedSize];
            byte[] ub = new byte[fatEntry.uncompressedSize];
            raf.readFully(b);
            LzoCompressor.decompress(b, 0, b.length, ub, 0, ub.length);
            
            File outFile = new File(file + i);
            OutputStream os = new FileOutputStream(outFile);
            IOUtils.write(ub, os);
            os.close();
            
            getPixel(i, ub);
        }
    }

    /**
     * 00 FFFF
     * 02 50 00 width
     * 04 50 00 height
     * 06 10 80 ?
     * 08 1C 00 ?
     * 0A 13 C0 (19个透明色) (C0=透明色?)
     * 0C 01 00 E5 24 (1色  E5 24)
     * 10 0B C0 (11个透明色) (C0=透明色?)
     * 12 02 00 05 2D E5 24 (2色  05 2D E5 24)
     * 18 0E C0 (14个透明色) (C0=透明色?)
     * 1A 02 00 E5 24 E5 28 (2色  E5 24 E5 28)
     * 20 1F C0 (31个透明色) (C0=透明色?) 此处正好到第一行末尾80格
     * 22 00 00 4C 00 
     * 26 0E C0 (14个透明色)
     * 28 07 00 E5 28 05 29 E5 28 E5 28 05 2D E5 28 E5 28 (7色)
     * 38 06 C0 (6个透明色)
     * 3A 06 00 E5 28 E5 28
     */
    private void getPixel(int index, byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.getShort(); //00
        int width = buffer.getShort();
        int height = buffer.getShort();
        buffer.getShort(); //06
        buffer.getShort(); //08
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        int pixelPos = 0;
        while (buffer.hasRemaining()) {
            int word = buffer.getShort() & 0xFFFF;
            
            //check first 2 bits are '1'?
            boolean isTransparent = false;
            if ((word & 0xC000) == 0xC000) {
                isTransparent = true;
            }
            int repeatCount = word & 0x3FFF;
            
            if (word == 0) {
                System.out.println(pixelPos + " pixels finished");
                buffer.getShort(); //skip 2
                
            } else if (isTransparent) {
                System.out.println(repeatCount + " number of transparent color");
                for (int i = 0; i < repeatCount; i++) {
                    bi.setRGB(pixelPos % width, pixelPos / width, 0); //transparent
                    pixelPos++;
                }
                
            } else {
                System.out.println(repeatCount + " number of untransparent color");
                for (int i = 0; i < repeatCount; i++) {
                    Color color = null;;
                    if (isColorDepth8) {
                        int colorIndex = buffer.get() & 0xFF;
                        color = colors[colorIndex];
                    } else {
                        int rgb555 = buffer.getShort() & 0xFFFF;
                        color = ColorUtil.rgb555ToRgb888(rgb555);
                    } 
                    
                    bi.setRGB(pixelPos % width, pixelPos / width, color.getRGB());
                    pixelPos++;
                }
            }

        }
        
        ImageIO.write(bi, "PNG", new File(file + "__" + index + ".png"));
    }
        
    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int offset;
        public int compressedSize;
        public int uncompressedSize;
        public int width;
        public int height;
        public int compressedSize2;
        public int uncompressedSize2;

        public void decode(ByteBuffer buffer) throws Exception {
            offset = buffer.getInt();
            compressedSize = buffer.getInt();
            uncompressedSize = buffer.getInt();
            compressedSize2 = buffer.getInt();
            uncompressedSize2 = buffer.getInt();
            buffer.getInt();
            buffer.getInt();
            buffer.getInt();
            width = buffer.getShort();
            height = buffer.getShort();
            System.out.println(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
