package org.xpen.capcom.aceattorney.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
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

public class Img {
    
    protected String fileName;
    protected Map<Integer, Color[]> paletteMap = new HashMap<>();
    
    /**
     * Palette: use 4 bit color(16 colors)
     * Tile: each byte represents 2 pixels
     */
    public void handle(Path path, int paletteFile, int width) throws Exception {
        fileName = path.toFile().getName();
        byte[] inBytes = Files.readAllBytes(path);
        
        Color[] palette = getPalette(paletteFile);
        
        int height = (inBytes.length - 4) / width * 2;

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bits = buffer.get() & 0xFF;
                int pixel1 = bits & 0xF;
                int pixel2 = (bits & 0xF0) >> 4;
                bi.setRGB(x, y, palette[pixel1].getRGB());
                x++;
                bi.setRGB(x, y, palette[pixel2].getRGB());
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
        Color[] colors = new Color[16];
        for (int i = 0; i < colors.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            colors[i] = c;
        }
        return colors;
    }

    private void writeFile(BufferedImage bi) throws Exception {
        File outFile = new File(UserSetting.rootOutputFolder, "romfile/" + fileName + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }
}
