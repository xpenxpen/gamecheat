package org.xpen.softstar.pal;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.softstar.pal.fileformat.TswFile;
import org.xpen.util.UserSetting;

/**
 * Pal 1 New
 * 新仙剑奇侠传
 *
 */
public class Pal1NewTsw {
    
    private static final Logger LOG = LoggerFactory.getLogger(Pal1NewTsw.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/pal1new/run/新仙剑奇侠传";
        UserSetting.rootOutputFolder = "F:/game/pal1new/run/新仙剑奇侠传/myex";
        String[] fileNames = {"All_Char.tsw", "All_Item.tsw", "all_magic.tsw", 
                "All_Map1.tsw", "All_Map2.tsw", "All_Sys.tsw"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	TswFile tswFile = new TswFile(fileName);
        	tswFile.decode();
        	tswFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
