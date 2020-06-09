package org.xpen.capcom.aceattorney.gk1.fileformat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.NintendoLz11Compressor;

import com.google.common.collect.Sets;

public class MiniTest2 {
    private static Color[] colors;
    private static Set<Integer> BACKGROUD_512 = Sets.newHashSet(
            3185, 3257);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files/myex/romfile";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex2";
        
        handleImg(7, 6, 64, false);
        handleImg(9, 256, true);
        handleImg(12, 64, false);
        handleImg(15, 256, true);
        handleImg(19, 18, 16, false);
        handleImg(25, 24, 64, false);
        handleImg(37, 96, false);
        handleImg(41, 40, 64, false);
        //handleImg(47, 96, false);
        
        for (int i = 836; i <= 853; i++) {
            handleImg(i, 835, 208, true);
        }
        
        handleImg(1182, 80, true);
        handleImg(1189, 64, true);
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
        handleImg(1380, 384, false);
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
        
        //1470~1516
        for (int i = 1470; i <= 1516; i += 2) {
            handleImg(i, 64, false);
        }
        
        //1522~1637
        for (int i = 1522; i <= 1637; i += 2) {
            handleImg(i, 1753, 64, false);
        }
        
        //1638~1669
        for (int i = 1638; i <= 1669; i += 1) {
            handleImg(i, 1753, 64, false);
        }
       
        //1671~1751
        for (int i = 1671; i <= 1751; i += 1) {
            handleImg(i, 1753, 64, false);
        }
        
        handleImg(1752, 64, false);
        handleImg(1754, 32, false);
        handleImg(1758, 64, false);
        handleImg(1760, 64, false);
        handleImg(1764, 64, false);
        handleImg(1780, 64, false);
        handleImg(1800, 64, false);
        handleImg(1809, 64, false);
        handleImg(1811, 64, false);
        handleImg(1814, 64, false);
        handleImg(1818, 64, false);
        
        //1850~1858
        for (int i = 1850; i <= 1858; i += 2) {
            handleImg(i, 64, false);
        }
        
        //1864~1868
        for (int i = 1864; i <= 1868; i += 2) {
            handleImg(i, 64, false);
        }
        
        handleImg(1926, 64, false);
        
        handleImg(1990, 64, false);
        handleImg(1996, 64, false);
        handleImg(1998, 64, false);
        
        //2005~2013
        for (int i = 2005; i <= 2013; i += 2) {
            handleImg(i, 64, false);
        }
        
        handleImg(2058, 64, false);
        handleImg(2060, 64, false);
        handleImg(2062, 64, false);
        
        //2078~2094
        for (int i = 2078; i <= 2094; i += 2) {
            handleImg(i, 64, false);
        }
        
        handleImg(2110, 64, false);
        handleImg(2112, 64, false);
        handleImg(2124, 64, false);
        handleImg(2130, 64, false);
        handleImg(2133, 64, false);
        handleImg(2135, 64, false);
        handleImg(2184, 64, false);
        handleImg(2186, 64, false);
        handleImg(2189, 64, false);
        handleImg(2197, 64, false);
        handleImg(2199, 64, false);
        handleImg(2205, 64, false);
        handleImg(2211, 64, false);
        handleImg(2221, 64, false);
        handleImg(2223, 64, false);
        handleImg(2226, 64, false);
        handleImg(2233, 64, false);
        handleImg(2239, 64, false);
        handleImg(2245, 64, false);
        handleImg(2251, 64, false);
        handleImg(2257, 64, false);
        handleImg(2263, 64, false);
        
        handleImg(2380, 64, false);
        handleImg(2382, 64, false);
        handleImg(2392, 64, false);
        handleImg(2394, 64, false);
        handleImg(2401, 64, false);
        handleImg(2408, 64, false);
        handleImg(2417, 64, false);
        handleImg(2419, 64, false);
        handleImg(2423, 64, false);
        handleImg(2426, 64, false);
        handleImg(2428, 64, false);
        handleImg(2611, 64, false);
        handleImg(2617, 64, false);
        handleImg(2773, 64, false);
        handleImg(2775, 64, false);
        handleImg(2777, 64, false);
        handleImg(2779, 64, false);
        
        //2793~2821
        for (int i = 2793; i <= 2821; i += 2) {
            handleImg(i, 256, true);
        }
        
        //2829~2839
        for (int i = 2829; i <= 2839; i += 2) {
            handleImg(i, 256, true);
        }
        
        //2845~3319
        for (int i = 2845; i <= 3319; i += 2) {
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
        
        //3631~3641
        for (int i = 3631; i <= 3641; i += 2) {
            handleImg(i, 3644, 256, false);
        }
        
        //3645~3669
        for (int i = 3645; i <= 3669; i += 2) {
            handleImg(i, 3697, 256, false);
        }
        
        handleImg(4027, 64, false);
        handleImg(4128, 64, false);
        handleImg(4153, 64, false);
        //handleImg(4188, 64, false);
        
        //4190~4232
        for (int i = 4190; i <= 4232; i += 2) {
            ImgLongFace imgLongFace = new ImgLongFace();
            int paletteFile = i + 1;
            handleImg(i, paletteFile, 256, false, imgLongFace);
        }
        
        handleImg(4236, 64, false);
        
        //4238~4276
        for (int i = 4238; i <= 4276; i += 2) {
            handleImg(i, 256, true);
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
        handleImg(imgFile, paletteFile, width, compress, false);
    }

    private static void handleImg(int imgFile, int paletteFile, int width, boolean compress, boolean forceHalfWidthPixel) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(imgFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, fourDigit);
        Img2 img = new Img2();
        img.lz10Or11Compressor = NintendoLz11Compressor.class;
        img.handle(path, paletteFile, -1, width, compress, forceHalfWidthPixel, false);
    }

    private static void handleImg(int imgFile, int paletteFile, int width, boolean compress, Img2 img) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(imgFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, fourDigit);
        img.handle(path, paletteFile, -1, width, compress, false, false);
    }

}
