package org.xpen.capcom.aceattorney.fileformat;

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

public class MiniTest {
    private static Color[] colors;

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files/myex";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex2";
        
        Img img = new Img();
        Path path = Paths.get(UserSetting.rootInputFolder, "romfile/0180");
        img.handle(path, 201, 256);
        

    }

}
