package org.xpen.elderscrolls5;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.creation.fileformat.BsaFile;
import org.xpen.util.UserSetting;

public class BsaExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(BsaExtracter.class);

    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "E:/aliBoxGames/games/5993/FarCry 3/data_win32";
        //UserSetting.rootOutputFolder = "E:/aliBoxGames/games/5993/myex";
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/skyrimdat";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/skyrimdat/myex";
    	//String[] fileNames = {"Skyrim - Interface.bsa"};
    	String[] fileNames = {"Skyrim - Interface"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            BsaFile bsaFile = new BsaFile(fileName);
            bsaFile.decode();
            bsaFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
