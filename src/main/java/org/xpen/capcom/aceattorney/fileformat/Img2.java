package org.xpen.capcom.aceattorney.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.NintendoLz11Compressor;

public class Img2 {
    
    protected String fileName;
    protected Map<Integer, Color[]> paletteMap = new HashMap<>();
    private int[][] tiles;
    private int tileCount;
    private Color[] palette;
    
    /**
     * Palette: use 4 bit color(16 colors)
     * Tile: each byte represents 2 pixels
     */
    public void handle(Path path, int paletteFile, int width, boolean compress) throws Exception {
        fileName = path.toFile().getName();
        byte[] inBytes = Files.readAllBytes(path);
        
        palette = getPalette(paletteFile);
        
        byte[] outBytes;
        inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
        //Lz11
        if (compress) {
            outBytes = NintendoLz11Compressor.decompress(inBytes);
        } else {
            outBytes = inBytes;
        }
        
        int height;
        
        if (palette.length == 16) {
            tileCount = outBytes.length / 32;
            height = outBytes.length * 2 / width;
        } else {
            tileCount = outBytes.length / 64;
            height = outBytes.length / width;
        }
        ByteBuffer buffer = ByteBuffer.wrap(outBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        getTile(buffer);
        

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int xTileCount = width / 8;
        int yTileCount = height / 8;
        int tileIndex = 0;
        
        for (int y = 0; y < yTileCount; y++) {
            for (int x = 0; x < xTileCount; x++) {
                for (int k = 0; k < 64; k++) {
                    bi.setRGB(x * 8 + k % 8, y * 8 + k / 8, palette[tiles[tileIndex][k]].getRGB());
                }
                tileIndex++;
            }
        }
        
        writeFile(bi);
        
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
        Path path = Paths.get(UserSetting.rootInputFolder, "romfile/" + fourDigit);
        byte[] inBytes = Files.readAllBytes(path);
        
        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(4);
        //BGR555
        Color[] colors = new Color[(inBytes.length - 4) / 2];
        for (int i = 0; i < colors.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            colors[i] = c;
        }
        return colors;
    }
    
    private void getTile(ByteBuffer buffer) throws Exception {
        tiles = new int[tileCount][];
        for (int i = 0; i < tileCount; i++) {
            //A tile is a block of 8x8 pixels
            tiles[i] = new int[64];
            for (int j = 0; j < 64; j++) {
                if (palette.length == 16) {
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
