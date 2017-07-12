package org.xpen.kingsoft.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class ImgFile {
    public static final int MAGIC_IMGV = 0x56474D49; //'IMGV'
    
    private static final Logger LOG = LoggerFactory.getLogger(ImgFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected String fileName;
    
    public Color[] colors;
    int width;
    int height;
    
    public ImgFile() {
    }
    
    public ImgFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * IMG File format
     *
     */
    public void decode() throws Exception {
    	try {
	        ByteBuffer buffer = ByteBuffer.allocate(16);
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        buffer.limit(16);
	        fileChannel.read(buffer);
	        buffer.flip();
	        
	        int magic = buffer.getInt();
	        if (magic != MAGIC_IMGV) {
	            throw new RuntimeException("bad magic");
	        }
	        
	        buffer.position(6);
	        width = buffer.getShort() + 1;
	        height = buffer.getShort() + 1;
	        
	        getPallete();
	        getPixel();
    	} catch (Exception e) {
    		System.out.println("ERROR--" + this.fileName);
    	}
        
    }
    
    private void getPallete() throws Exception {
        fileChannel.position(width * height + 16);
        
        colors = new Color[256];
        
        try {
	        for (int i = 0; i < colors.length; i++) {
	        	int r =  raf.readUnsignedByte() * 4;
	        	int g =  raf.readUnsignedByte() * 4;
	        	int b =  raf.readUnsignedByte() * 4;
	        	int a =  0xFF;
	        	colors[i] = new Color(r,g,b,a);
	        }
	        
        } catch (IllegalArgumentException e) {
            fileChannel.position(width * height + 16);
	        for (int i = 0; i < colors.length; i++) {
	        	int r =  raf.readUnsignedByte();
	        	int g =  raf.readUnsignedByte();
	        	int b =  raf.readUnsignedByte();
	        	int a =  0xFF;
	        	colors[i] = new Color(r,g,b,a);
	        }
        	
        }
    }

	private void getPixel() throws Exception {
        fileChannel.position(16);
        
	    int total = 0;
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    for (int j = 0; j < height; j++) {
	        for (int i = 0; i < width; i++) {
	            int colorIndex = raf.readUnsignedByte();
            	bi.setRGB(i, j, colors[colorIndex].getRGB());
		    }
	    	
	    }
	    
	    String baseName = FilenameUtils.getBaseName(this.fileName);

        File outFile = null;
        outFile = new File(UserSetting.rootOutputFolder, baseName + ".png");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
	}


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
}
