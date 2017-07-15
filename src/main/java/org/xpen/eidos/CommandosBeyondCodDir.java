package org.xpen.eidos;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.eidos.fileformat.DirFile;
import org.xpen.util.UserSetting;

/**
 * Commandos: Beyond the Call of Duty
 * 盟军敢死队:使命召唤
 *
 */
public class CommandosBeyondCodDir {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommandosBeyondCodDir.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/MengJunGansdui/盟军敢死队";
        UserSetting.rootOutputFolder = "F:/game/MengJunGansdui/盟军敢死队/myex";
        String[] fileNames = {"War_mp"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	DirFile dirFile = new DirFile(fileName);
        	dirFile.decode();
        	dirFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
