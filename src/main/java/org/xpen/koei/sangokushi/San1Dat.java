package org.xpen.koei.sangokushi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.sangokushi.fileformat.San1To5EightColorFile;
import org.xpen.util.UserSetting;

public class San1Dat {
    
    private static final Logger LOG = LoggerFactory.getLogger(San1Dat.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/f/VirtualNes/DOS/rom/san/San1";
        UserSetting.rootOutputFolder = "G:/f/VirtualNes/DOS/rom/san/San1/myex";
    	String[] fileNames = {"PICDATA.DAT"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	San1To5EightColorFile datFile = new San1To5EightColorFile(1, fileName);
        	datFile.decode();
        	datFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
