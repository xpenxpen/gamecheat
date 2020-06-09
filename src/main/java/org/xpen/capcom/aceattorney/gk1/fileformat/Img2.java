package org.xpen.capcom.aceattorney.gk1.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class Img2 {
    
    protected String fileName;
    protected Map<Integer, Color[]> paletteMap = new HashMap<>();
    protected int[][] tiles;
    protected int[] maps;
    protected int tileCount;
    protected Color[] palette;
    protected int width;
    protected int height;
    protected byte[] outBytes;
    public Class<?> lz10Or11Compressor;
    private boolean forceHalfWidthPixel;
    private boolean hasMap;
    private boolean compress;
    
    /**
     * Palette: 16 color/256 color
     * Tile: each byte represents 2/1 pixels
     */
    public void handle(Path path, int paletteFile, int mapFile, int width,
            boolean compress, boolean forceHalfWidthPixel, boolean hasMap) throws Exception {
        this.width = width;
        this.forceHalfWidthPixel = forceHalfWidthPixel;
        this.hasMap = hasMap;
        this.compress = compress;
        fileName = path.toFile().getName();
        byte[] inBytes = Files.readAllBytes(path);
        
        palette = getPalette(paletteFile);
        
        if (hasMap) {
            getMapFromFile(mapFile);
        }
        
        //Lz11
        if (compress) {
            Method method = lz10Or11Compressor.getMethod("decompress", byte[].class);
            outBytes = (byte[])method.invoke(null, inBytes);
        } else {
            outBytes = inBytes;
        }
        
        getHeight();
        
        ByteBuffer buffer = ByteBuffer.wrap(outBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        getTile(buffer);
        

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        drawImg(width, height, bi);
        
        writeFile(bi);
        
    }
    
    public void getHeight() {
        if (palette.length == 16 || forceHalfWidthPixel) {
            tileCount = outBytes.length / 32;
            height = outBytes.length * 2 / width;
        } else {
            tileCount = outBytes.length / 64;
            height = outBytes.length / width;
        }
        
        if (hasMap) {
            height = maps.length * 64 / width;
        }
    }

    public void drawImg(int width, int height, BufferedImage bi) {
        int xTileCount = width / 8;
        int yTileCount = height / 8;
        int tileIndex = 0;
        
        if (hasMap) {
            for (int y = 0; y < yTileCount; y++) {
                for (int x = 0; x < xTileCount; x++) {
                    int currentTile = maps[tileIndex] & 0x3FF;
                    for (int k = 0; k < 64; k++) {
                        bi.setRGB(x * 8 + k % 8, y * 8 + k / 8, palette[tiles[currentTile][k]].getRGB());
                    }
                    tileIndex++;
                }
            }
            
        } else {
            for (int y = 0; y < yTileCount; y++) {
                for (int x = 0; x < xTileCount; x++) {
                    for (int k = 0; k < 64; k++) {
                        bi.setRGB(x * 8 + k % 8, y * 8 + k / 8, palette[tiles[tileIndex][k]].getRGB());
                    }
                    tileIndex++;
                }
            }
        }
    }

    private Color[] getPalette(int paletteFile) throws Exception {
        if (!paletteMap.containsKey(paletteFile)) {
            Color[] palette = getPaletteFromFile(paletteFile);
            paletteMap.put(paletteFile, palette);
        }
        
        return paletteMap.get(paletteFile);
    }
    
    private Color[] getPaletteFromFile(int paletteFile) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(paletteFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, fourDigit);
        byte[] inBytes = Files.readAllBytes(path);
        
        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //BGR555
        Color[] colors = new Color[inBytes.length / 2];
        for (int i = 0; i < colors.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            colors[i] = c;
        }
        return colors;
    }
    
    private int[] getMapFromFile(int mapFile) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(mapFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, fourDigit);
        byte[] inBytes = Files.readAllBytes(path);
        //Lz11
        if (compress) {
            Method method = lz10Or11Compressor.getMethod("decompress", byte[].class);
            inBytes = (byte[])method.invoke(null, inBytes);
        } else {
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        maps = new int[inBytes.length / 2];
        for (int i = 0; i < maps.length; i++) {
            short no = buffer.getShort();
            maps[i] = no;
        }
        return maps;
    }
    
    private void getTile(ByteBuffer buffer) throws Exception {
        tiles = new int[tileCount][];
        for (int i = 0; i < tileCount; i++) {
            //A tile is a block of 8x8 pixels
            tiles[i] = new int[64];
            for (int j = 0; j < 64; j++) {
                if (palette.length == 16 || forceHalfWidthPixel) {
                    int bits = buffer.get() & 0xFF;
                    int pixel1 = bits & 0xF;
                    int pixel2 = (bits & 0xF0) >> 4;
                    tiles[i][j] = pixel1;
                    j++;
                    tiles[i][j] = pixel2;
                } else {
                    tiles[i][j] = buffer.get() & 0xFF;
                }
            }
        }
    }

    private void writeFile(BufferedImage bi) throws Exception {
        File outFile = new File(UserSetting.rootOutputFolder, "romfile/" + fileName + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }
}
