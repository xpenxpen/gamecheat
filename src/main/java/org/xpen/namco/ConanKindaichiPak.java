package org.xpen.namco;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.namco.fileformat.PakFile;
import org.xpen.util.UserSetting;

/**
 * Detective Conan & Kindaichi Case Files: Chance Meeting of Two Great Detectives
 * 名侦探柯南&金田一少年之事件簿 两大名侦探的相逢
 * 名探偵コナン&金田一少年の事件簿 めぐりあう2人の名探偵
 * 
 *
 */
public class ConanKindaichiPak {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConanKindaichiPak.class);
    private static final String FILE_SUFFIX_PAK = "pak";
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100126/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100126/root/myex";
        String folderName = "pack";
        
        //all_info_mess 所有文字
        //all_info_main 证物图片
        //background 背景图片
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        
            
        Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_PAK}, false);
        for (File f : files) {
            totalCount++;
            String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
            PakFile pakFile = new PakFile();
            try {
                pakFile.decode(folderName, oldFileNameWithoutExt, f);
                handleCount++;
            } catch (Exception e) {
                LOG.warn("Error occurred, skip {}", f.getName());
            } finally {
                pakFile.close();
            }
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);
    }

}
