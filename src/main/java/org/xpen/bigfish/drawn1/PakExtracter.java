package org.xpen.bigfish.drawn1;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.bigfish.fileformat.PakFile;
import org.xpen.util.UserSetting;

public class PakExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/Program Files/Drawn The Painted Tower";
        UserSetting.rootOutputFolder = "G:/Program Files/Drawn The Painted Tower/myex";
    	String[] fileNames = {"data"};
        
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
