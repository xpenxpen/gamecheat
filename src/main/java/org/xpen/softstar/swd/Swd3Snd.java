package org.xpen.softstar.swd;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.softstar.pal.fileformat.TswFile;
import org.xpen.util.UserSetting;

/**
 * 轩辕剑3
 *
 */
public class Swd3Snd {
    
    private static final Logger LOG = LoggerFactory.getLogger(Swd3Snd.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/swd3/run/轩辕剑3云和山的彼端";
        UserSetting.rootOutputFolder = "F:/game/swd3/run/轩辕剑3云和山的彼端/myex";
        String[] fileNames = {"all.snd"};
        
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
