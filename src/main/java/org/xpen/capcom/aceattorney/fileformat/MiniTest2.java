package org.xpen.capcom.aceattorney.fileformat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.xpen.util.UserSetting;

public class MiniTest2 {
    private static Color[] colors;

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files/myex";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex2";
        
        Img2 img = new Img2();
        for (int i = 836; i <= 853; i++) {
            String fourDigit = StringUtils.leftPad(String.valueOf(i), 4, '0');
            Path path = Paths.get(UserSetting.rootInputFolder, "romfile/" + fourDigit);
            img.handle(path, 835, 128, true);
        }
        
        Path path = Paths.get(UserSetting.rootInputFolder, "romfile/0009");
        img.handle(path, 10, 192, true);
        
        path = Paths.get(UserSetting.rootInputFolder, "romfile/1328");
        img.handle(path, 1329, 512, false);
        
        path = Paths.get(UserSetting.rootInputFolder, "romfile/1330");
        img.handle(path, 1331, 512, false);
        
        path = Paths.get(UserSetting.rootInputFolder, "romfile/1332");
        img.handle(path, 1333, 512, false);
        

    }

}
