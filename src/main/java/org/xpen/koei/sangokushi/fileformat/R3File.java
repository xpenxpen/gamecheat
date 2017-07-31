package org.xpen.koei.sangokushi.fileformat;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class R3File {
    
    public static final int MAGIC_LS11 = 0x3131534C; //LS11
    
    private static final Logger LOG = LoggerFactory.getLogger(R3File.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    public Color[] san3Palletes;
    int width;
    int height;
    BufferedImage bi;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private byte[] dicts;
    
    private String fileName;
    
    
    public R3File(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName + ".r3"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * LS11 File format
     * 0x00--0x10  LS11
     * 0x10--0x110 dict
     * 0x110--  FAT
     *
     */
    public void decode() throws Exception {
        //getPallete();
        //getWidthHeight();
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(0x10);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(0x10);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_LS11) {
           throw new RuntimeException("bad magic");
        }
        
        dicts = new byte[256];
        for (int i = 0; i < dicts.length; i++) {
            dicts[i] = raf.readByte();
        }
        
        
        while (true) {
            buffer = ByteBuffer.allocate(0x0C);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.limit(0x0C);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            if (!fatEntry.decode(buffer)) {
                break;
            }
            fatEntries.add(fatEntry);
        }
    }
    
    private void decodeDat() throws Exception {
        for (int index = 0; index < fatEntries.size(); index++) {
            FatEntry fatEntry = fatEntries.get(index);
            
            byte[] bytes = new byte[fatEntry.compressedSize];
            byte[] outBytes = new byte[fatEntry.uncompressedSize];
            raf.seek(fatEntry.offset);
            raf.readFully(bytes);
            
            if (fatEntry.compressedSize == fatEntry.uncompressedSize) {
                outBytes = bytes;
            } else {
                decodeLs11(bytes, outBytes, fatEntry);
            }
            
            File outFile = null;
            String threeDigit = StringUtils.leftPad(String.valueOf(index + 1), 3, '0');
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(outBytes, os);
            os.close();
        }
    }
    

    private void decodeLs11(byte[] inBytes, byte[] outBytes, FatEntry fatEntry) throws Exception {
        Ls11 ls11 = new Ls11(inBytes, dicts);
        ls11.decode(outBytes);
    }
    
    private void getPallete() throws Exception {
        
        san3Palletes = new Color[8];
        int[] uu = new int[] {0x000000, 0xef3f00, 0x004fef, 0xcf4fef, 0x4faf0f, 0xefbf00, 0x00dfef, 0xefefef};
        
        for (int i = 0; i < san3Palletes.length; i++) {
            int r =  uu[i] & 0xFF;
            int g =  (uu[i] >>> 8) & 0xFF;
            int b =  (uu[i] >>> 16) & 0xFF;
            int a =  0xFF;
            san3Palletes[i] = new Color(r,g,b,a);
        }
        
    }

    private void getWidthHeight() {
        if (this.fileName.equals("KAODATA")) {
            width = 64;
            height = 80;
        } else if (this.fileName.equals("KOEI")) {
            width = 256;
            height = 508;
        } else if (this.fileName.equals("MONTAGE")) {
            width = 64;
            height = 5808;
        }
        
    }

    private void getPixel(int index) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(width*height/8*3);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(width*height/8*3);
        fileChannel.read(buffer);
        buffer.flip();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        //三国志里面是用8色的，所以是用每三个字节表示8个像素。
        //比如说三个字节是11000000,11100001,00110101，则用相应位的三个组合在一起就可以得到实际的图像数据是6,6,3,1,0,1,0,3
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width / 8; i++) {
                int b1 = buffer.get() & 0xFF;
                int b2 = buffer.get() & 0xFF;
                int b3 = buffer.get() & 0xFF;
                
                int[] p = new int[8];
                for (int k = 0; k < 8; k++) {
                    p[k] = ((b1 >>> (7 - k)) & 0x1) *4 + ((b2 >>> (7 - k)) & 0x1) * 2 + ((b3 >>> (7 - k)) & 0x1);
                }
                
                for (int k = 0; k < 8; k++) {
                    bi.setRGB(i * 8 + k, j, san3Palletes[p[k]].getRGB());
                }
            }
        }
        
        File outFile = null;
        String threeDigit = StringUtils.leftPad(String.valueOf(index + 1), 3, '0');
        outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".png");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public class FatEntry {
        public String fname;
        public int offset;
        public int uncompressedSize;
        public int compressedSize;

        public boolean decode(ByteBuffer buffer) throws Exception {
            compressedSize = buffer.getInt();
            if (compressedSize == 0) {
                return false;
            }
            uncompressedSize = buffer.getInt();
            offset = buffer.getInt();
            LOG.debug(toString());
            return true;
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
