package org.xpen.koei.nobunaga;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.sangokushi.fileformat.San1To5EightColorFile;
import org.xpen.util.UserSetting;

public class Nobu4Dat {
    
    private static final Logger LOG = LoggerFactory.getLogger(Nobu4Dat.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/信长之野望/信长之野望4/nobu4";
        UserSetting.rootOutputFolder = "D:/game/信长之野望/信长之野望4/nobu4/myex";
    	String[] fileNames = {"KAODATA.DAT", "MONDATA.DAT"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	San1To5EightColorFile datFile = new San1To5EightColorFile(24, fileName);
        	datFile.decode();
        	datFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
