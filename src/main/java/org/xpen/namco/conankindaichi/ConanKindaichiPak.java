package org.xpen.namco.conankindaichi;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Detective Conan & Kindaichi Case Files: Chance Meeting of Two Great Detectives
 * 名侦探柯南&金田一少年之事件簿 两大名侦探的相逢
 * 名探偵コナン&金田一少年の事件簿 めぐりあう2人の名探偵
 * 1765/1886
 *
 */
public class ConanKindaichiPak {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConanKindaichiPak.class);
    private static final String FILE_SUFFIX_PAK = "pak";
    
    public static void main(String[] args) throws Exception {
//        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100126/root";
//        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100126/root/myex";
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8003356/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8003356/root/myex";
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
