package org.xpen.chunsoft.zeroescape;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.chunsoft.zeroescape.fileformat.CfsiFile;
import org.xpen.util.UserSetting;

/**
 * voice:
 * 00001-- Japanese total 7975
 * 09001-- English  total 7967
 *
 */
public class ZeroEscape3CfsiExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(ZeroEscape3CfsiExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/3DMGAME/Zero Escape";
        UserSetting.rootOutputFolder = "E:/3DMGAME/Zero Escape/myex";
    	//String[] fileNames = {"bgm", "voice", "00000000"};
    	String[] fileNames = {"00000000"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	CfsiFile cfsiFile = new CfsiFile(fileName);
        	cfsiFile.decode();
        	cfsiFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
