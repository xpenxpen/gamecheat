package org.xpen.namco.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class MesD {
    private static Color[] palette;
    private static byte[] bytes;
    private static ByteBuffer buffer;

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100126/root/myex";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100126/root/myex2";
        
        Path folder = Paths.get(UserSetting.rootInputFolder, "pack/all_info_main/001");
        bytes = Files.readAllBytes(folder);
        
        getPalette();
        handle(64);


    }

    private static void handle(int width) throws Exception {
        int height = (bytes.length - 64) / width;

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bits = buffer.get() & 0xFF;
                if (bits >= 32) {
                    bits = 0;
                }
                bi.setRGB(x, y, palette[bits].getRGB());
            }
        }
        
        writeFile(bi);
    }

    private static void getPalette() {
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //BGR555
        palette = new Color[32];
        for (int i = 0; i < palette.length; i++) {
            short colorBits = buffer.getShort();
            Color c = ColorUtil.bgr555ToRgb888(colorBits);
            palette[i] = c;
        }
    }

    private static void writeFile(BufferedImage bi) throws Exception {
        File outFile = new File(UserSetting.rootOutputFolder, "pack/all_info_main/001" + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

}
