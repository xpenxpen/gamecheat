package org.xpen.level5.layton;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.level5.layton.fileformat.ArcFile;
import org.xpen.util.UserSetting;

/**
 * Professor Layton 2: Diabolical Box
 * 雷顿教授2：恶魔之箱
 *
 */
public class Layton2Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton2Img.class);
    private static final String FILE_SUFFIX_ARC = "arc";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/game/nds/8100436/root/data_lt2";
        UserSetting.rootOutputFolder = "D:/soft/game/nds/8100436/root/data_lt2/myex";
    	String[] folderNames = {"ani/bgani", "ani/sub", "ani/title"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        
        for (String folderName: folderNames) {
        	LOG.debug("---------Starting {}", folderName);
        	
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_ARC}, false);
            for (File f : files) {
                totalCount++;
                try {
                    ArcFile.decode(folderName, f);
                    handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip {}", f.getName());
                }
            }
        	
            
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);

    }

}
