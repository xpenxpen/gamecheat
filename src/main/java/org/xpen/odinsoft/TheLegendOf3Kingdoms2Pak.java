package org.xpen.odinsoft;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.odinsoft.fileformat.PakFile;
import org.xpen.util.UserSetting;

/**
 * The Legend of Three Kingdoms 2
 * 三国群英传2
 *
 */
public class TheLegendOf3Kingdoms2Pak {
    
    private static final Logger LOG = LoggerFactory.getLogger(TheLegendOf3Kingdoms2Pak.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/Sango2/Sango2";
        UserSetting.rootOutputFolder = "F:/game/Sango2/Sango2/myex";
        String[] fileNames = {"Sango2"};
        
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
