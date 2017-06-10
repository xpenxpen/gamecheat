package org.xpen.artifexmundi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.artifexmundi.fileformat.CubFile;
import org.xpen.util.UserSetting;

/**
 * Enigmatis: The Ghosts of Maple Creek
 *
 */
public class Enigmatis1Cub {
    
    private static final Logger LOG = LoggerFactory.getLogger(Enigmatis1Cub.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/Enigmatis_TheGhostsofMapleCreek";
        UserSetting.rootOutputFolder = "F:/game/Enigmatis_TheGhostsofMapleCreek/myex";
        String[] fileNames = {"Data", "Data_hires", "data2", "Game", "Game_hires",
        		"Game_hires_ja", "Game_ja"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	CubFile cubFile = new CubFile(fileName);
        	cubFile.decode();
        	cubFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
