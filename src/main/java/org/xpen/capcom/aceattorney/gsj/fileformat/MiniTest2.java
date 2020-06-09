package org.xpen.capcom.aceattorney.gsj.fileformat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.xpen.capcom.aceattorney.gk1.fileformat.Img2;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.NintendoLz10Compressor;

import com.google.common.collect.Sets;

public class MiniTest2 {
    private static Color[] colors;
    private static Set<Integer> BACKGROUD_512 = Sets.newHashSet(
            1000, 2255, 2258, 2297, 2318, 3461, 3473);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8110105/root/myex/data";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8110105/root/myex2";
        
        //handleImg(3, 256, true);
        //handleImg(129, 64, true);
        //handleImg(136, 768, true);
        handleMapImg(136, 137, 135, 256, true);
        handleImg(138, 64, true);
        handleMapImg(141, 256, true);
        handleMapImg(144, 256, true);
        handleMapImg(149, 256, true);
        handleMapImg(152, 256, true);
        handleMapImg(160, 256, true);
        handleMapImg(164, 256, true);
        handleMapImg(166, 167, 162, 256, true);
        handleImg(168, 64, true);
        handleMapImg(171, 256, true);
        handleImg(173, 64, true);
        handleMapImg(176, 256, true);
        handleImg(178, 64, true);
        handleMapImg(181, 256, true);
        handleImg(183, 64, true);
        handleMapImg(187, 256, true);
        handleMapImg(189, 190, 185, 256, true);
        handleMapImg(195, 256, true);
        handleMapImg(197, 198, 193, 256, true);
        handleMapImg(208, 256, true);
        handleMapImg(221, 256, true);
        handleMapImg(223, 224, 219, 256, true);
        handleImg(225, 64, true);
        handleMapImg(228, 229, 227, 256, true);
        handleImg(230, 64, true);
        handleMapImg(233, 234, 232, 256, true);
        handleImg(235, 64, true);
        handleMapImg(238, 256, true);
        handleMapImg(243, 256, true);
        handleImg(245, 64, true);
        handleMapImg(248, 256, true);
        handleMapImg(252, 256, true);
        handleMapImg(254, 255, 250, 256, true);
        handleMapImg(259, 256, true);
        handleMapImg(264, 256, true);
        handleMapImg(269, 256, true);
        handleMapImg(274, 256, true);
        handleMapImg(282, 256, true);
        handleMapImg(287, 256, true);
        handleMapImg(290, 256, true);
        handleMapImg(295, 256, true);
        handleMapImg(298, 256, true);
        handleMapImg(303, 256, true);
        handleMapImg(306, 256, true);
        handleMapImg(311, 256, true);
        handleImg(313, 64, true);
        handleMapImg(316, 256, true);
        handleImg(318, 64, true);
        handleMapImg(321, 256, true);
        handleImg(323, 64, true);
        handleMapImg(326, 256, true);
        handleMapImg(331, 256, true);
        handleMapImg(336, 256, true);
        handleMapImg(341, 256, true);
        handleMapImg(347, 256, true);
        handleMapImg(349, 350, 345, 256, true);
        handleMapImg(354, 256, true);
        handleMapImg(359, 256, true);
        handleMapImg(364, 256, true);
        handleMapImg(369, 256, true);
        handleMapImg(389, 256, true);
        handleMapImg(394, 256, true);
        handleMapImg(399, 256, true);
        handleMapImg(405, 256, true);
        handleMapImg(407, 408, 403, 256, true);
        
        //412~477
        for (int i = 412; i <= 477; i += 5) {
            handleMapImg(i, 256, true);
            handleImg(i + 2, 64, true);
        }
        
        handleMapImg(483, 256, true);
        handleMapImg(485, 486, 481, 256, true);
        
        handleMapImg(515, 256, true);
        handleMapImg(520, 256, true);
        handleMapImg(575, 256, true);
        handleMapImg(593, 256, true);
        handleMapImg(684, 256, true);
        handleMapImg(689, 256, true);
        handleMapImg(692, 256, true);
        handleMapImg(705, 256, true);
        handleMapImg(708, 256, true);
        
        //738~982
        for (int i = 738; i <= 982; i += 2) {
            handleImg(i, i + 1, 64, true);
        }
        
        handleMapImg(991, 256, true);
        
        //994~1051
        for (int i = 994; i <= 1051; i += 3) {
            if (BACKGROUD_512.contains(i)) {
                handleImg(i, 512, true);
            } else {
                handleImg(i, 256, true);
            }
        }
        handleMapImg(1009, 256, true);
        handleMapImg(1042, 256, true);
        handleMapImg(1045, 256, true);
        
        handleMapImg(1054, 256, true);
        handleMapImg(1057, 256, true);
        handleMapImg(1060, 256, true);
        handleImg(1063, 256, true);
        handleMapImg(1066, 256, true);
        handleMapImg(1069, 256, true);
        handleImg(1072, 256, true);
        handleImg(1074, 256, true);
        
        //1399~1411
        for (int i = 1399; i <= 1411; i += 2) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        //1413~1436 map??
        
        //1437~1445
        for (int i = 1437; i <= 1445; i += 2) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        //1755~1765
        for (int i = 1755; i <= 1765; i += 2) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        //1804~1810
        for (int i = 1804; i <= 1810; i += 2) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        handleImg(1825, 1826, 256, true, true);
        
        //1999~2005
        for (int i = 1999; i <= 2005; i += 2) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        handleImg(2241, 256, true);
        handleImg(2244, 256, true);
        handleImg(2247, 256, true);
        handleImg(2255, 2257, 512, true);
        
        //2258~2318
        for (int i = 2258; i <= 2318; i += 3) {
            if (BACKGROUD_512.contains(i)) {
                handleImg(i, 512, true);
            } else {
                handleImg(i, 256, true);
            }
        }
        handleMapImg(2291, 256, true);
        handleMapImg(2294, 256, true);
        
        
        //3446~3455
        for (int i = 3446; i <= 3455; i += 3) {
            handleMapImg(i, 256, true);
        }
        
        //3458~3473
        for (int i = 3458; i <= 3473; i += 3) {
            if (BACKGROUD_512.contains(i)) {
                handleImg(i, 512, true);
            } else {
                handleImg(i, 256, true);
            }
        }
        
        handleMapImg(3476, 256, true);
        handleImg(3479, 256, true);
        
        //3482~3506
        for (int i = 3482; i <= 3506; i += 3) {
            handleMapImg(i, 256, true);
        }
        
        //3512~3521
        for (int i = 3512; i <= 3521; i += 3) {
            if (BACKGROUD_512.contains(i)) {
                handleImg(i, 512, true);
            } else {
                handleImg(i, 256, true);
            }
        }
        
        handleMapImg(3524, 256, true);
        handleImg(3527, 256, true);
        
        handleImg(4844, 256, true);
        handleMapImg(4979, 256, true);
        handleImg(5012, 256, true);
        handleMapImg(5015, 256, true);
        
        //5072~5102
        for (int i = 5072; i <= 5102; i += 2) {
            handleImg(i, 256, false);
        }
        
        
//        //6462~6482
//        for (int i = 6462; i <= 6482; i += 3) {
//            handleImg(i, i + 1, 64, true, true);
//        }
        
        
        //6559~6619
        for (int i = 6559; i <= 6619; i += 3) {
            handleImg(i, i + 1, 256, true, true);
        }
        
        handleMapImg(6624, 256, true);
        
        //6626~6670
        for (int i = 6626; i <= 6670; i += 2) {
            handleImg(i, 256, true);
        }
        
        handleImg(6685, 256, true);
        handleImg(6723, 6722, 128, true);
        
        //6746~7246
        for (int i = 6746; i <= 7246; i += 2) {
            handleImg(i, 64, true);
        }
        
        //handleImg(7739, 64, false);
        handleImg(7871, 64, true);
        handleImg(7886, 32, true);
        handleImg(7912, 64, true);
        handleImg(7914, 64, true);

    }

    /**
     * Palette file = img file + 1
     */
    private static void handleImg(int imgFile, int width, boolean compress) throws Exception {
        int paletteFile = imgFile + 1;
        handleImg(imgFile, paletteFile, width, compress);
    }

    private static void handleImg(int imgFile, int paletteFile, int width, boolean compress) throws Exception {
        handleImg(imgFile, paletteFile, -1, width, compress, false, false);
    }

    private static void handleImg(int imgFile, int paletteFile, int mapFile, int width, boolean compress, boolean forceHalfWidthPixel, boolean hasMap) throws Exception {
        String fourDigit = StringUtils.leftPad(String.valueOf(imgFile), 4, '0');
        Path path = Paths.get(UserSetting.rootInputFolder, fourDigit);
        Img2 img = new Img2();
        img.lz10Or11Compressor = NintendoLz10Compressor.class;
        img.handle(path, paletteFile, mapFile, width, compress, forceHalfWidthPixel, hasMap);
    }

    private static void handleImg(int imgFile, int paletteFile, int width, boolean compress, boolean forceHalfWidthPixel) throws Exception {
        handleImg(imgFile, paletteFile, -1, width, compress, forceHalfWidthPixel, false);
    }

    private static void handleMapImg(int imgFile, int width, boolean compress) throws Exception {
        handleImg(imgFile, imgFile + 1, imgFile - 1, width, compress, false, true);
    }

    private static void handleMapImg(int imgFile, int paletteFile, int mapFile, int width, boolean compress) throws Exception {
        handleImg(imgFile, paletteFile, mapFile, width, compress, false, true);
    }

}
