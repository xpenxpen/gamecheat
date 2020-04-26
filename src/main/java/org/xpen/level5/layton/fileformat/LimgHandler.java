package org.xpen.level5.layton.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.Ntfs;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class LimgHandler implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(LimgHandler.class);
    public static final int MAGIC_LIMG = 0x474D494C; //LIMG
    
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
        LimgEntry limgEntry = new LimgEntry();
        limgEntry.decode(buffer);
    }
    
    
    public void close() throws Exception {
    }
    
    /**
     * LIMG File format
     * 4 Magic: 'LIMG'
     * 4 Header Length: Must be 0x30
     * 8 Unknown 0x5001580C
     * 2 Map Offset 7C
     * 2 Map count 0300
     * 2 Tile Offset 067C
     * 2 Tile count 42
     * 2 Palette Count 00
     * 2 ColorCount 10
     * 2 Width 0100
     * 2 Height C0
     * 16 Unknown
     * 
     * Palette
     * Map(NTFS)
     * Tile (8*8)
     */
   public class LimgEntry {
        public int headerLength;
        public int width;
        public int height;
        public int mapOffset;
        public int mapCount;
        public int tileOffset;
        public int tileCount;
        public int paletteCount;
        public int colorCount; //If is 16, then one tile only use 4 bits
        private Color[][] colors;
        private int[][] tiles;
        public boolean tileUseHalfByte = false;
        
        public void decode(ByteBuffer buffer) throws Exception {
            int magic = buffer.getInt();
            if (magic != MAGIC_LIMG) {
                throw new RuntimeException("bad magic");
            }
            
            headerLength = buffer.getInt();
            buffer.position(buffer.position() + 8);
            
            mapOffset = buffer.getShort();
            mapCount = buffer.getShort();
            tileOffset = buffer.getShort();
            tileCount = buffer.getShort();
            paletteCount = buffer.getShort();
            colorCount = buffer.getShort();
            if (colorCount == 16) {
                tileUseHalfByte = true;
            }
            
            width = buffer.getShort();
            height = buffer.getShort();
            
            //System.out.println(this);
            
            getPallete(buffer);
            getTile(buffer);
            buffer.position(mapOffset);
            getPixel(buffer);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }

        private void getPallete(ByteBuffer buffer) throws Exception {
            buffer.position(headerLength);
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
            buffer.position(tileOffset);
            tiles = new int[tileCount][];
            for (int i = 0; i < tileCount; i++) {
                //A tile is a block of 8x8 pixels
                tiles[i] = new int[64];
                for (int j = 0; j < 64; j++) {
                    //tileCount may be bigger than buffer
                    if (!buffer.hasRemaining()) {
                        break;
                    }
                    int bits = buffer.get();
                    if (tileUseHalfByte) {
                        tiles[i][j] = bits & 0xF;
                        j++;
                        tiles[i][j] = (bits & 0xF0) >> 4;
                    } else {
                        tiles[i][j] = bits & 0xFF;
                    }
                    
                }
            }
        }
        
        private void getPixel(ByteBuffer buffer) throws Exception {
            BufferedImage bi = Ntfs.getPixel(buffer, width, height, tiles, colors);
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
