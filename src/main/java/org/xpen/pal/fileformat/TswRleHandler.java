package org.xpen.pal.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.LzoCompressor;

public class TswRleHandler implements FileTypeHandler {
	
    private static final Logger LOG = LoggerFactory.getLogger(TswRleHandler.class);
    
    private ByteBuffer buffer;
    private BufferedImage bi;
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String datFileName;
    private String fname;
    private byte[] bytes;

    public TswRleHandler() {
    }

	@Override
	public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;

        buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        
        clear();
        decodeFat();
        decodeDat();
 	}

    private void clear() {
        fatEntries.clear();
        LOG.debug("STARTING fname={}", fname);
    }

    private void decodeFat() throws Exception {
        
        buffer.position(6);
        int fatCount = buffer.getShort();
        int unknown1 = buffer.getShort();
        int unknown2 = buffer.getShort();
        //System.out.println("fatCount="+fatCount+",unknown1="+unknown1+",unknown2="+unknown2);
        
        for (int i = 0; i < fatCount; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
            fatEntry.decode(buffer);
        }
                
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            buffer.position(fatEntry.offset);
            byte[] b = new byte[fatEntry.compressedSize];
            byte[] ub = new byte[fatEntry.uncompressedSize];
            buffer.get(b);
            LzoCompressor.decompress(b, 0, b.length, ub, 0, ub.length);
            
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
            int repeatCount = buffer.get() & 0xFF;
            int alphaValue = buffer.get() & 0xFF;
            
            if (repeatCount == 0 && alphaValue == 0) {
                //System.out.println(pixelPos + " pixels finished");
                buffer.getShort(); //skip 2
                
            } else if (alphaValue == 0xC0) {
                //System.out.println(repeatCount + " number of transparent color");
                for (int i = 0; i < repeatCount; i++) {
                    bi.setRGB(pixelPos % width, pixelPos / width, 0); //transparent
                    pixelPos++;
                }
                
            } else if (alphaValue == 0x00) {
                //System.out.println(repeatCount + " number of untransparent color");
                for (int i = 0; i < repeatCount; i++) {
                    int rgb555 = buffer.getShort() & 0xFFFF;
//                    int r5 = rgb565 & 0x1f;
//                    int g6 = (rgb565 >>> 5) & 0x3f;
//                    int b5 = (rgb565 >>> 11) & 0x1f;
                    int b5 = rgb555 & 0x1f;
                    int g5 = (rgb555 >>> 5) & 0x1f;
                    int r5 = (rgb555 >>> 10) & 0x1f;
                    // Scale components up to 8 bit: 
                    // Shift left and fill empty bits at the end with the highest bits,
                    // so 00000 is extended to 000000000 but 11111 is extended to 11111111
//                    int r = (b5 << 3) | (b5 >> 2);
//                    int g = (g6 << 2) | (g6 >> 4);
//                    int b = (r5 << 3) | (r5 >> 2);
                    int b = (b5 << 3) | (b5 >> 2);
                    int g = (g5 << 3) | (g5 >> 2);
                    int r = (r5 << 3) | (r5 >> 2);
//                    int b = b5;
//                    int g = g6;
//                    int r = r5;
                    Color color = new Color(r, g, b, 255);
                    
                    bi.setRGB(pixelPos % width, pixelPos / width, color.getRGB());
                    pixelPos++;
                }
            } else {
                LOG.error("buffer.position()="+buffer.position() + ", alphaValue="+alphaValue);
                throw new RuntimeException("RLE format error?");
            }

        }
        
        File outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + fname + "_" + index + ".png");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
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
            //total 0x24 bytes
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
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}