package org.xpen.capcom.aceattorney.fileformat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.xpen.util.UserSetting;

import com.google.common.collect.Sets;

public class MiniTest2 {
    private static Color[] colors;
    private static Set<Integer> BACKGROUD_512 = Sets.newHashSet(
            3185, 3257);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files/myex";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex2";
        
        Img2 img = new Img2();
        for (int i = 836; i <= 853; i++) {
            String fourDigit = StringUtils.leftPad(String.valueOf(i), 4, '0');
            Path path = Paths.get(UserSetting.rootInputFolder, "romfile/" + fourDigit);
            img.handle(path, 835, 128, true);
        }
        
        handleImg(9, 256, true);
        handleImg(15, 256, false);
        
        handleImg(1278, 304, false);
        handleImg(1280, 304, false);
        handleImg(1287, 480, false);
        handleImg(1292, 592, false);
        handleImg(1294, 592, false);
        
        handleImg(1302, 640, false);
        handleImg(1309, 256, false);
        handleImg(1314, 256, false);
        handleImg(1316, 256, false);
        handleImg(1323, 256, false);
        
        handleImg(1328, 512, false);
        handleImg(1330, 512, false);
        handleImg(1332, 512, false);
        
        handleImg(1341, 768, false);
        handleImg(1348, 608, false);
        handleImg(1355, 512, false);
        handleImg(1357, 512, false);
        handleImg(1363, 768, false);
        handleImg(1368, 768, false);
        handleImg(1370, 768, false);
        handleImg(1377, 384, false);
        handleImg(1382, 384, false);
        handleImg(1387, 512, false);
        handleImg(1389, 512, false);
        handleImg(1393, 320, false);
        handleImg(1398, 384, false);
        
        handleImg(1405, 576, false);
        handleImg(1408, 384, false);
        handleImg(1413, 384, false);
        handleImg(1418, 608, false);
        
        handleImg(1423, 512, false);
        handleImg(1425, 512, false);
        handleImg(1432, 448, false);
        handleImg(1437, 448, false);
        handleImg(1442, 448, false);
        handleImg(1447, 512, false);
        handleImg(1449, 512, false);
        handleImg(1456, 304, false);
        
        handleImg(2867, 256, true);
        handleImg(2887, 256, true);
        handleImg(2909, 256, true);
        handleImg(2927, 256, true);
        handleImg(3011, 256, true);
        
        //3061~3319
        for (int i = 3061; i <= 3319; i += 2) {
            if (BACKGROUD_512.contains(i)) {
                handleImg(i, 512, true);
            } else {
                handleImg(i, 256, true);
            }
        }
        
        handleImg(3322, 256, true);
        handleImg(3325, 256, true);
        
        //3338~3424
        for (int i = 3338; i <= 3424; i += 2) {
            handleImg(i, 256, true);
        }
        
        handleImg(3426, 64, true);
        handleImg(3428, 64, true);
        
        handleImg(3436, 256, true);
        handleImg(3439, 832, false);
        handleImg(3441, 832, false);
        
        //3443~3449
        for (int i = 3443; i <= 3449; i += 2) {
            handleImg(i, 256, true);
        }
        
        //3456~3460
        for (int i = 3456; i <= 3460; i += 2) {
            handleImg(i, 256, true);
        }
        
        //3463~3493
        for (int i = 3463; i <= 3493; i += 2) {
            handleImg(i, 256, true);
        }
        
        //3502~3506 unknown
        //for (int i = 3502; i <= 3506; i += 2) {
        //    handleImg(i, 256, false);
        //}
        
        handleImg(3514, 64, false);
        
        //3528~3538
        for (int i = 3528; i <= 3538; i += 2) {
            handleImg(i, 64, false);
        }
        
        //handleImg(3556, 3555, 256, true);
        
        //3595~3621
        for (int i = 3595; i <= 3621; i += 2) {
            handleImg(i, 832, false);
        }
        
        handleImg(4281, 256, false);
        

    }

    /**
     * Palette file = img file + 1
     */
    private static void handleImg(int imgFile, int width, boolean compress) throws Exception {
        int paletteFile = imgFile + 1;
        handleImg(imgFile, paletteFile, width, compress);
    }

    private static void handleImg(int imgFile, int paletteFile, int width, boolean compress) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(imgFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, "romfile/" + fourDigit);
        Img2 img = new Img2();
        img.handle(path, paletteFile, width, compress);
    }

}
