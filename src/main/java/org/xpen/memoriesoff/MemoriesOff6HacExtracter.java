package org.xpen.memoriesoff;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class MemoriesOff6HacExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(MemoriesOff6HacExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/72180";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/72180/myex";
    	String[] fileNames = {"DATA"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	HacFile hacFile = new HacFile(fileName);
        	hacFile.decode();
        	hacFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
