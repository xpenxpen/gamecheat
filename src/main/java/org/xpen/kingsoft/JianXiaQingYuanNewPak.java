package org.xpen.kingsoft;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.kingsoft.fileformat.PakFile;
import org.xpen.util.UserSetting;

public class JianXiaQingYuanNewPak {
    
    private static final Logger LOG = LoggerFactory.getLogger(JianXiaQingYuanNewPak.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/新剑侠情缘/Data";
        UserSetting.rootOutputFolder = "F:/game/新剑侠情缘/Data/myex";
    	String[] fileNames = {"asf", "font", "ini", "map", "mpc", "Patch", "script", "sound"};
    	//String[] fileNames = {"sound"};
        
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
