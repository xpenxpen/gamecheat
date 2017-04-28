package org.xpen.pal4;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.CpkFile;
import org.xpen.util.UserSetting;

public class CpkExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(CpkExtracter.class);

    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "E:/aliBoxGames/games/10087/Skyrim/Data";
        //UserSetting.rootOutputFolder = "E:/aliBoxGames/games/10087/myex";
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/paladindat";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/paladindat/myex";
    	//String[] fileNames = {"MatFX"};
    	String[] fileNames = {"MatFX"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            CpkFile cpkFile = new CpkFile(fileName);
            cpkFile.decode();
            cpkFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
