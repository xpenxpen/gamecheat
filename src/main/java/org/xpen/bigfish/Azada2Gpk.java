package org.xpen.bigfish;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.bigfish.fileformat.Bfg83File;
import org.xpen.util.UserSetting;

public class Azada2Gpk {
    
    private static final Logger LOG = LoggerFactory.getLogger(Azada2Gpk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/Program Files/azadaancientmagic";
        UserSetting.rootOutputFolder = "G:/Program Files/azadaancientmagic/myex";
    	String[] fileNames = {"data", "channel"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	Bfg83File bfg83File = new Bfg83File(fileName);
        	bfg83File.decode();
        	bfg83File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
