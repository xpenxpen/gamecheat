package org.xpen.namco.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class ImgHandler implements FileTypeHandler {
	
    private static final Logger LOG = LoggerFactory.getLogger(ImgHandler.class);
	
	private String datFileName;
	private String fname;
	private String extension;
    private boolean keepOldFileName;
	private byte[] bytes;
	private Color[] palette;
	private int colorCount;
    private ByteBuffer buffer;
    private int width;
    private int height;
    private BufferedImage bi;

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
    	//LOG.debug("newFileName={}", newFileName);
		getPallete();
        getPixel();
        writeFile();
	}

	private void getPallete() throws Exception {
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //BGR555
        for (int i = 0; i < palette.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            palette[i] = c;
        }
	}

	private void getPixel() throws Exception {
        int height = (bytes.length - colorCount * 2) / width;

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bits = buffer.get() & 0xFF;
                if (bits >= colorCount) {
                    bits = 0;
                }
                bi.setRGB(x, y, palette[bits].getRGB());
            }
        }
	}

	private void writeFile() throws Exception {
        File outFile = null;
        String oldFileNameWithoutExt = fname;
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + oldFileNameWithoutExt + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
	}

}
