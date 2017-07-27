package org.xpen.koei.sangokushi.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class San1To5EightColorFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(San1To5EightColorFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    public Color[] palletes;
    int width;
    int height;
    BufferedImage bi;
    
    private int type;
    private String fileName;
    
    
    public San1To5EightColorFile(int type, String fileName) throws Exception {
    	this.type = type;
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * DAT File format
     *
     */
    public void decode() throws Exception {
        getPallete();
        getWidthHeight();
        decodeDat();
    }


    private void decodeDat() throws Exception {
        int fileSize = (int)raf.length();
        int fileCount = fileSize / 3 * 8/ (width*height);
        LOG.debug("fileCount={}", fileCount);;
        for (int i = 0; i < fileCount; i++) {
            getPixel(i);
        }
    }

    private void getPallete() throws Exception {
        
        palletes = new Color[8];
        int[][] p = new int[6][];
        //TODO 1 palletes
        p[1] = new int[] {0x000000, 0xff5050, 0x5050ff, 0xff50ff, 0x50f850, 0xfff850, 0x50f8ff, 0xfff8ff};
        p[2] = new int[] {0x000000, 0xff5050, 0x5050ff, 0xff50ff, 0x50f850, 0xfff850, 0x50f8ff, 0xfff8ff};
        p[3] = new int[] {0x000000, 0xef3f00, 0x004fef, 0xcf4fef, 0x4faf0f, 0xefbf00, 0x00dfef, 0xefefef};
        p[4] = new int[] {0x001f2f, 0x7f3f1f, 0x1f3faf, 0x4f7fbf, 0x1f6f3f, 0x8f7f3f, 0x7fafcf, 0xafcfcf};
        //TODO 5 palletes
        p[5] = new int[] {0x001f2f, 0x7f3f1f, 0x1f3faf, 0x4f7fbf, 0x1f6f3f, 0x8f7f3f, 0x7fafcf, 0xafcfcf};
        
        int[] selectedPalletes = p[type];
        for (int i = 0; i < palletes.length; i++) {
            int r =  selectedPalletes[i] & 0xFF;
            int b =  (selectedPalletes[i] >>> 8) & 0xFF;
            int g =  (selectedPalletes[i] >>> 16) & 0xFF;
            int a =  0xFF;
            palletes[i] = new Color(r,g,b,a);
        }
        
    }

    private void getWidthHeight() {
    	if (type == 1) {
	        if (this.fileName.equals("PICDATA.DAT")) {
	            width = 32;
	            height = 40;
	        }
    	} else if (type == 2) {
	        if (this.fileName.equals("KAODATA.DAT")) {
	            width = 64;
	            height = 40;
	        } else if (this.fileName.equals("MONTAGE.DAT")) {
	            width = 64;
	            height = 2112;
	        }
    	} else if (type == 3) {
	        if (this.fileName.equals("KAODATA.DAT")) {
	            width = 64;
	            height = 80;
	        } else if (this.fileName.equals("KOEI.DAT")) {
	            width = 256;
	            height = 508;
	        } else if (this.fileName.equals("MONTAGE.DAT")) {
	            width = 64;
	            height = 5808;
	        }
    	} if (type == 4) {
	        if (this.fileName.equals("KAODATA.S4")) {
	            width = 64;
	            height = 80;
	        } else if (this.fileName.equals("KAODATA2.S4")) {
	            width = 64;
	            height = 80;
	        } else if (this.fileName.equals("KAODATAP.S4")) {
	            width = 64;
	            height = 80;
	        }
    	} if (type == 5) {
	        if (this.fileName.equals("KAODATA.S5")) {
	            width = 64;
	            height = 80;
	        } else if (this.fileName.equals("KAODATAP.S5")) {
	            width = 64;
	            height = 80;
	        } else if (this.fileName.equals("KAOEX.S5")) {
	            width = 64;
	            height = 80;
	        }
    	}
        
    }

    private void getPixel(int index) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(width*height/8*3);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(width*height/8*3);
        fileChannel.read(buffer);
        buffer.flip();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
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
                    bi.setRGB(i * 8 + k, j, palletes[p[k]].getRGB());
                }
            }
        }
        
        File outFile = null;
        String threeDigit = StringUtils.leftPad(String.valueOf(index + 1), 3, '0');
        outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".bmp");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "bmp", outFile);
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
}
