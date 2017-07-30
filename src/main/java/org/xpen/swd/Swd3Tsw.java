package org.xpen.swd;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.TswFile;
import org.xpen.util.UserSetting;

/**
 * Swd 3
 * 轩辕剑3云和山的彼端
 *
 */
public class Swd3Tsw {
    
    private static final Logger LOG = LoggerFactory.getLogger(Swd3Tsw.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/swd3/run/轩辕剑3云和山的彼端";
        UserSetting.rootOutputFolder = "F:/game/swd3/run/轩辕剑3云和山的彼端/myex";
        String[] fileNames = {"all_char.tsw", "all_item.tsw", "all_magic.tsw", 
                "all_map1.tsw", "all_map2.tsw", "all_sys.tsw"};
        
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
