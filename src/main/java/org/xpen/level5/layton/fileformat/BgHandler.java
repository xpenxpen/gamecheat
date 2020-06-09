package org.xpen.level5.layton.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.Ntfs;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class BgHandler implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(BgHandler.class);
    
    protected String subFolderName;
    protected String fileName;
    private byte[] bytes;
    
    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.subFolderName = datFileName;
        this.fileName = newFileName;
        this.bytes = b;
        decodeDat();
    }
        
    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        BgEntry bgEntry = new BgEntry();
        bgEntry.decode(buffer);
    }
    
    
    public void close() throws Exception {
    }
    
    /**
     * Bg File format
     * 4 Palette color count
     * Palette color count * 2   Colors in BGR555 format
     * 4 Tile count
     * Tile count * 64   A tile is a block of 8x8 pixels
     * 2 width
     * 2 height
     * width * height  Map(NTFS)
     */
   public class BgEntry {
        public int width;
        public int height;
        private Color[][] colors;
        private int[][] tiles;
        
        public void decode(ByteBuffer buffer) throws Exception {
            getPallete(buffer);
            getTile(buffer);
            width = buffer.getShort();
            height = buffer.getShort();
            //System.out.println("colors="+colors.length+",tiles="+tiles.length+",width="+width+",height="+height);
            getPixel(buffer);
       }

        private void getPallete(ByteBuffer buffer) throws Exception {
            int colorCount = buffer.getInt();
            //BGR555
            colors = new Color[1][];
            colors[0] = new Color[colorCount];
            for (int i = 0; i < colors[0].length; i++) {
                short colorBits = buffer.getShort();
                Color c = ColorUtil.bgr555ToRgb888(colorBits);
                colors[0][i] = c;
            }
        }
        
        private void getTile(ByteBuffer buffer) throws Exception {
            int tileCount = buffer.getInt();
            tiles = new int[tileCount][];
            for (int i = 0; i < tileCount; i++) {
                //A tile is a block of 8x8 pixels
                tiles[i] = new int[64];
                for (int j = 0; j < 64; j++) {
                    tiles[i][j] = buffer.get() & 0xFF;
                }
            }
        }
        
        private void getPixel(ByteBuffer buffer) throws Exception {
            //For example, width * height = 32 * 24, then real image size is 256 * 192
            BufferedImage bi = Ntfs.getPixel(buffer, width * 8, height * 8, tiles, colors);
            writeFile(bi);
        }

        private void writeFile(BufferedImage bi) throws Exception {
            File outFile = null;
            String oldFileNameWithoutExt = fileName;
            File parent = new File(UserSetting.rootOutputFolder, subFolderName);
            outFile = new File(parent, oldFileNameWithoutExt + ".png");
            
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            ImageIO.write(bi, "PNG", outFile);
        }
    }

}
