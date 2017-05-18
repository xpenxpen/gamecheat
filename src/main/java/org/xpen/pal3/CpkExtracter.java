package org.xpen.pal3;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.CpkFile;
import org.xpen.util.UserSetting;

public class CpkExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(CpkExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/pal3";
        UserSetting.rootOutputFolder = "F:/game/pal3/myex";
    	//String[] fileNames = {"basedata/basedata", "music/music", "movie/movie" , "movie/movie_end"};
    	String[] fileNames = {"scene/M01",
    			"scene/M02",
    			"scene/M03",
    			"scene/M04",
    			"scene/M05",
    			"scene/M06",
    			"scene/m08",
    			"scene/M09",
    			"scene/m10",
    			"scene/m11",
    			"scene/M15",
    			"scene/M16",
    			"scene/M17",
    			"scene/M18",
    			"scene/M19",
    			"scene/M20",
    			"scene/M21",
    			"scene/M22",
    			"scene/M23",
    			"scene/M24",
    			"scene/M25",
    			"scene/M26",
    			"scene/Q01",
    			"scene/Q02",
    			"scene/Q03",
    			"scene/Q04",
    			"scene/Q05",
    			"scene/Q06",
    			"scene/Q07",
    			"scene/Q08",
    			"scene/Q09",
    			"scene/Q10",
    			"scene/Q11",
    			"scene/Q12",
    			"scene/Q13",
    			"scene/Q14",
    			"scene/Q15",
    			"scene/Q16",
    			"scene/Q17",
    			"scene/T01",
    			"scene/T02"};
    	
        
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
