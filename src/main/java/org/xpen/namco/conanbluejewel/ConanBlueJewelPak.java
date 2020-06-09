package org.xpen.namco.conanbluejewel;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.namco.conankindaichi.FileTypeDetector;
import org.xpen.namco.conankindaichi.PakFile;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Detective Conan: The Blue Jewel's Rondo
 * 名侦探柯南 苍蓝宝石的轮舞曲
 * 名探偵コナン　蒼き宝石の輪舞曲
 * 2181/2349
 *
 */
public class ConanBlueJewelPak {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConanBlueJewelPak.class);
    private static final String FILE_SUFFIX_PAK = "pak";
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100127/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100127/root/myex";
        String folderName = "pack";
        
        //all_info_mess 所有文字
        //all_info_main 证物图片
        //background 背景图片
        //chara 人物半身像
        //system_mess
        //talk_char 人物头像
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        HandleCount countPair = new HandleCount();
            
        Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_PAK}, false);
        for (File f : files) {
            String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
            PakFile pakFile = new PakFile();
            pakFile.fileTypeDetector = FileTypeDetector.class;
//            if (!oldFileNameWithoutExt.equals("chara")) {
//                continue;
//            }
            
            try {
                pakFile.decode(folderName, oldFileNameWithoutExt, f, countPair);
            } catch (Exception e) {
                LOG.warn("Error occurred, skip " + f.getName(), e);
            } finally {
                pakFile.close();
            }
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
