package org.xpen.koei;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.fileformat.Ls1112;
import org.xpen.util.UserSetting;

/**
 * 大航海时代4威力加强版
 *
 */
public class UnchartedWaters4Pk {
    
    private static final Logger LOG = LoggerFactory.getLogger(UnchartedWaters4Pk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/DK4PK";
        UserSetting.rootOutputFolder = "D:/game/DK4PK/myex";
    	String[] fileNames = {"bustup.dk4", "EventBG1.dk4", "EventBG2.dk4", "EventBG3.dk4", "EventBG4.dk4",
    	        "EventBG5.dk4", "EventBG6.dk4", "EventBG7.dk4", "EventBG8.dk4"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            Ls1112 ls1112File = new Ls1112(fileName);
            ls1112File.type = 12;
            ls1112File.decode();
            ls1112File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
