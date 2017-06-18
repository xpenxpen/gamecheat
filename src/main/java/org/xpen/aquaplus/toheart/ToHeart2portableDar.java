package org.xpen.aquaplus.toheart;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.aquaplus.fileformat.DarFile;
import org.xpen.util.UserSetting;

public class ToHeart2portableDar {
    
    private static final Logger LOG = LoggerFactory.getLogger(ToHeart2portableDar.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/psp/toheart2/NoLabel/PSP_GAME/USRDIR";
        UserSetting.rootOutputFolder = "D:/psp/toheart2/NoLabel/PSP_GAME/USRDIR/myex";
    	String[] darFileNames = {"data", "at3Voice"};
    	boolean[] isNoCompress = {false, true};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (int i = 0; i < darFileNames.length; i++) {
        	String darFileName = darFileNames[i];
        	LOG.debug("---------Starting {}", darFileName);
            
        	DarFile darFile = new DarFile(darFileName);
        	darFile.isNoCompress = isNoCompress[i];
        	darFile.decode();
        	darFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
