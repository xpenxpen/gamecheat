package org.xpen.bigfish;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.bigfish.fileformat.Bfg82File;
import org.xpen.util.UserSetting;

public class Azada1Gpk {
    
    private static final Logger LOG = LoggerFactory.getLogger(Azada1Gpk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/azada";
        UserSetting.rootOutputFolder = "F:/game/azada/myex";
    	String[] fileNames = {"data", "channel"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	Bfg82File bfg82File = new Bfg82File(fileName);
        	bfg82File.decode();
        	bfg82File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
