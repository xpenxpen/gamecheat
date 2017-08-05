package org.xpen.koei;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.fileformat.Ls1112;
import org.xpen.util.UserSetting;

/**
 * 大航海时代4威力加强版
 *
 */
public class UnchartedWaters4Pk {
    
    private static final Logger LOG = LoggerFactory.getLogger(UnchartedWaters4Pk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/DK4PK";
        UserSetting.rootOutputFolder = "D:/game/DK4PK/myex";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        File folder = new File(UserSetting.rootInputFolder);
        Collection<File> listFiles = FileUtils.listFiles(folder, new String[]{"DK4", "dk4"}, false);
        
        for (File file: listFiles) {
            LOG.debug("---------Starting {}", file.getName());
            
            Ls1112 ls1112File = new Ls1112(file.getName());
            ls1112File.type = 12;
            try {
                ls1112File.decode();
            } catch (Exception e) {
                //ignore
            } finally {
                ls1112File.close();
            }
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
