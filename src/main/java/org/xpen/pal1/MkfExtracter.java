package org.xpen.pal1;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.MkfFile;
import org.xpen.util.UserSetting;

public class MkfExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(MkfExtracter.class);

    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "F:/game/pal4/gamedata";
        //UserSetting.rootOutputFolder = "F:/game/pal4/myex";
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/dos/games-master/dos/pal1/D Pal.cdrom";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/dos/games-master/dos/pal1/D Pal.cdrom/myex";
    	//String[] fileNames = {"2d", "database", "Effect", "MatFX", "palobject"};
    	String[] fileNames = {"FBP"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	MkfFile mkfFile = new MkfFile(fileName);
        	mkfFile.decode();
        	mkfFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
