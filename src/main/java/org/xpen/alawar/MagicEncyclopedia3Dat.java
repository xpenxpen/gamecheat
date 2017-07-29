package org.xpen.alawar;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.popcap.fileformat.X7x7mFile;
import org.xpen.util.UserSetting;

/**
 * Magic Encyclopedia 3: Illusions
 * 魔法全书3：幻象
 *
 */
public class MagicEncyclopedia3Dat {
    
    private static final Logger LOG = LoggerFactory.getLogger(MagicEncyclopedia3Dat.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/70803";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/70803/myex";
    	String[] fileNames = {"game.dat"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	X7x7mFile pakFile = new X7x7mFile(fileName);
        	pakFile.decode();
        	pakFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
