package org.xpen.artifexmundi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.artifexmundi.fileformat.CubFile;
import org.xpen.util.UserSetting;

/**
 * Eventide: Slavic Fable
 *
 */
public class Eventide1Cub {
    
    private static final Logger LOG = LoggerFactory.getLogger(Eventide1Cub.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/108734";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/108734/myex";
        String[] fileNames = {"Game", "Game_768", "Game_768_zh", "Game_900", "Game_900_zh",
        		"Game_1080", "Game_1080_900", "Game_1080_900_zh", "Game_1080_zh", "Game_zh"};
        
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
