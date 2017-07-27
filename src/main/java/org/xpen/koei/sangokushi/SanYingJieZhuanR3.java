package org.xpen.koei.sangokushi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.sangokushi.fileformat.R3File;
import org.xpen.util.UserSetting;

public class SanYingJieZhuanR3 {
    
    private static final Logger LOG = LoggerFactory.getLogger(SanYingJieZhuanR3.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/san/三国志英杰传";
        UserSetting.rootOutputFolder = "D:/game/san/三国志英杰传/myex";
    	String[] fileNames = {"hexbmap"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	R3File r3File = new R3File(fileName);
        	r3File.decode();
        	r3File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
