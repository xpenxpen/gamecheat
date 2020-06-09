package org.xpen.namco.conankindaichi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class ImgHandler implements FileTypeHandler {
	
    private static final Logger LOG = LoggerFactory.getLogger(ImgHandler.class);
	
	protected String datFileName;
	protected String fname;
	private String extension;
    private boolean keepOldFileName;
    protected byte[] bytes;
    protected Color[] palette;
    protected int colorCount;
    protected ByteBuffer buffer;
    protected int width;
    protected int height;
    protected BufferedImage bi;

    public ImgHandler() {
    }

	public ImgHandler(int colorCount, int width) {
        this.width = width;
        this.colorCount = colorCount;
        this.palette = new Color[colorCount];
	}

    @Override
	public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
		this.datFileName = datFileName;
		this.fname = newFileName;
		this.bytes = b;
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    	//LOG.debug("newFileName={}", newFileName);
		getPallete();
        getHeight();
        getPixel();
        writeFile();
	}

    protected void getPallete() throws Exception {
        //BGR555
        for (int i = 0; i < palette.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            palette[i] = c;
        }
	}

    protected void getPixel() throws Exception {

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bits = buffer.get() & 0xFF;
                
                if (colorCount == 16) {
                    int bit1 = bits & 0xF;
                    int bit2 = (bits & 0xF0) >> 4;
                    bi.setRGB(x, y, palette[bit1].getRGB());
                    bi.setRGB(x + 1, y, palette[bit2].getRGB());
                    x += 1;
                } else {
                    if (bits >= colorCount) {
                        bits = 0;
                    }
                    
                    bi.setRGB(x, y, palette[bits].getRGB());
                }
            }
        }
	}

    protected void getHeight() {
        height = (bytes.length - colorCount * 2) / width;
    }

    protected void writeFile() throws Exception {
        File outFile = null;
        String oldFileNameWithoutExt = fname;
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + oldFileNameWithoutExt + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
	}

}
