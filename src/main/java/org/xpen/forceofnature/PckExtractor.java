package org.xpen.forceofnature;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.farcry3.UserSetting;
import org.xpen.forceofnature.pck.Entry;
import org.xpen.forceofnature.pck.PckFile;

public class PckExtractor {
    
    private static final Logger LOG = LoggerFactory.getLogger(PckExtractor.class);

	public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/baiduyundownload/Force.of.Nature.v1.0.12.CHS.1.2-ALI213/Force of Nature/Data";
        UserSetting.rootOutputFolder = "E:/baiduyundownload/Force.of.Nature.v1.0.12.CHS.1.2-ALI213/Force of Nature/myex";
    	String[] fileNames = {"Data", "Textures", "Objects", "Sounds"};
    	//String[] fileNames = {"Sounds"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            PckFile pckFile = new PckFile(fileName);
            pckFile.decode();
            pckFile.close();
        }
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
 
	}

}
