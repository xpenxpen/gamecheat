package org.xpen.blossomtale;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.blossomtale.fileformat.BlossomTalesFile;
import org.xpen.util.UserSetting;

public class BlossomTales {
    
    private static final Logger LOG = LoggerFactory.getLogger(BlossomTales.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/BlossomTalesTheSleepingKing";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/BlossomTalesTheSleepingKing/ex";
    	String[] fileNames = {"BlossomTales.exe"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	BlossomTalesFile btFile = new BlossomTalesFile(fileName);
        	btFile.decode();
        	btFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
