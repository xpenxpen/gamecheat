package org.xpen.kingsoft.swordnew;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.kingsoft.fileformat.PakFile;
import org.xpen.util.UserSetting;

public class PakExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/newsword/run/Data";
        UserSetting.rootOutputFolder = "F:/game/newsword/run/Data/myex";
    	String[] fileNames = {"Patch"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	PakFile pakFile = new PakFile(fileName);
        	pakFile.decode();
        	pakFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
