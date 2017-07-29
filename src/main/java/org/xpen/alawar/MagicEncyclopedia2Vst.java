package org.xpen.alawar;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.alawar.format.VstFile;
import org.xpen.util.UserSetting;

/**
 * Magic Encyclopedia 2: Moon Light
 * 魔法全书2：月光
 *
 */
public class MagicEncyclopedia2Vst {
    
    private static final Logger LOG = LoggerFactory.getLogger(MagicEncyclopedia2Vst.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/42624";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/42624/myex";
    	String[] fileNames = {"magic2.vst"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	VstFile vstFile = new VstFile(fileName);
        	vstFile.decode();
        	vstFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
