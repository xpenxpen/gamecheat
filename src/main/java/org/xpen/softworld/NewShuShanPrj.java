package org.xpen.softworld;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.softworld.fileformat.PrjFile;
import org.xpen.util.UserSetting;

/**
 * 新蜀山剑侠传
 *
 */
public class NewShuShanPrj {
    
    private static final Logger LOG = LoggerFactory.getLogger(NewShuShanPrj.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/新蜀山剑侠传/Bitmap";
        UserSetting.rootOutputFolder = "D:/game/新蜀山剑侠传/Bitmap/myex";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Collection<File> listFiles = FileUtils.listFiles(new File(UserSetting.rootInputFolder), new String[]{"prj"}, false);

        for (File file: listFiles) {
            String fileName = file.getName();
        	LOG.debug("---------Starting {}", fileName);
            
            PrjFile prjFile = new PrjFile(fileName);
            prjFile.decode();
            prjFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
