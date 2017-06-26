package org.xpen.avalanche.studios;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.avalanche.studios.fileformat.ArcFile;
import org.xpen.avalanche.studios.fileformat.TabFile;
import org.xpen.util.UserSetting;

public class RenegadeOpsArc {
    
    private static final Logger LOG = LoggerFactory.getLogger(RenegadeOpsArc.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/RenegadeOps/archives_win32";
        UserSetting.rootOutputFolder = "D:/game/RenegadeOps/archives_win32/myex";
    	String[] fileNames = {"game0"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (int i = 0; i < fileNames.length; i++) {
        	String fileName = fileNames[i];
        	LOG.debug("---------Starting {}", fileName);
            
        	TabFile tabFile = new TabFile(fileName);
        	tabFile.decode();
        	tabFile.close();
        	ArcFile arcFile = new ArcFile(fileName);
        	arcFile.setFatEntries(tabFile.getFatEntries());
        	arcFile.decode();
        	arcFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
