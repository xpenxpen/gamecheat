package org.xpen.koei.sangokushi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.sangokushi.fileformat.San1To5EightColorFile;
import org.xpen.util.UserSetting;

public class San2Dat {
    
    private static final Logger LOG = LoggerFactory.getLogger(San2Dat.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/f/VirtualNes/DOS/rom/san/San2";
        UserSetting.rootOutputFolder = "G:/f/VirtualNes/DOS/rom/san/San2/myex";
    	String[] fileNames = {"KAODATA.DAT", "MONTAGE.DAT"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	San1To5EightColorFile datFile = new San1To5EightColorFile(2, fileName);
        	datFile.decode();
        	datFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
