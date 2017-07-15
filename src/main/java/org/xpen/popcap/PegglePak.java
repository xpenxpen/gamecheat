package org.xpen.popcap;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.popcap.fileformat.PakFile;
import org.xpen.util.UserSetting;

public class PegglePak {
    
    private static final Logger LOG = LoggerFactory.getLogger(PegglePak.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/636";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/636/myex";
    	String[] fileNames = {"main"};
        
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