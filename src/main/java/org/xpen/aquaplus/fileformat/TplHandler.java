package org.xpen.aquaplus.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

public class TplHandler implements FileTypeHandler {
	
    private static final Logger LOG = LoggerFactory.getLogger(TplHandler.class);
	
	private String datFileName;
	private String fname;
	private String extension;
    private boolean keepOldFileName;
	private byte[] bytes;
	private Color[] colors;
    private int width;
    private int height;
    private BufferedImage bi;

	public TplHandler(String extension, boolean keepOldFileName) {
        this.extension = extension;
        this.keepOldFileName = keepOldFileName;
	}

	@Override
	public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
		this.datFileName = datFileName;
		this.fname = newFileName;
		this.bytes = b;
    	//LOG.debug("newFileName={}", newFileName);
		getPallete();
        getPixel();
        writeFile();
	}

	private void getPallete() throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.position(16);
        height = buffer.getShort();
        width = buffer.getShort();
        
        buffer.position(width*height+48);
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int r =  buffer.get() & 0xFF;
        	int g =  buffer.get() & 0xFF;
        	int b =  buffer.get() & 0xFF;
        	int a =  buffer.get() & 0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
	}

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int offset = 48;
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        int bigRound = width / 16 * 8;
        for (int i = 0; i < width*height/16; i++) {
        	int line = 8 * (i % (width / 16)) + i % bigRound / (width / 16) + i / bigRound * bigRound;
        	//LOG.debug("i={},line={}", i, line);
        	buffer.position(offset + line * 16);
            for (int j = 0; j < 16; j++) {
            	int colorIndex = buffer.get() & 0xFF;
            	int x = i % (width/16) * 16 + j;
            	int y = i / (width/16);
            	//LOG.debug("x={},y={}", x, y);
            	bi.setRGB(x,  y, colors[colorIndex].getRGB());
            }
        }
        
	}

	private void writeFile() throws Exception {
        File outFile = null;
        String oldFileNameWithoutExt = fname.substring(0, fname.lastIndexOf('.'));
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + oldFileNameWithoutExt + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
	}

}
