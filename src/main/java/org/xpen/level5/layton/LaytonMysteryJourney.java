package org.xpen.level5.layton;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.level5.layton.fileformat.Hp10File;
import org.xpen.util.UserSetting;

public class LaytonMysteryJourney {
    
    private static final Logger LOG = LoggerFactory.getLogger(LaytonMysteryJourney.class);
    private static final String FILE_SUFFIX_CIMG = "cimg";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/com.Level5.LaytonMJ";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/com.Level5.LaytonMJ/myex";
        
        String[] fileNames = {"main.17213.com.Level5.LaytonMJ.obb"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);
            
            Hp10File file = new Hp10File(fileName);
            file.decode();
            file.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        //System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);

    }

}
