package org.xpen.popcap;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.popcap.fileformat.SafFile;
import org.xpen.util.UserSetting;

public class FeedingFrenzy2Saf {
    
    private static final Logger LOG = LoggerFactory.getLogger(FeedingFrenzy2Saf.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/13";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/13/myex";
    	String[] fileNames = {"FF2"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	SafFile safFile = new SafFile(fileName);
        	safFile.decode();
        	safFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
