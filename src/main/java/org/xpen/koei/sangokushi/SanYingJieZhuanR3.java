package org.xpen.koei.sangokushi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.fileformat.Ls1112;
import org.xpen.util.UserSetting;

public class SanYingJieZhuanR3 {
    
    private static final Logger LOG = LoggerFactory.getLogger(SanYingJieZhuanR3.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/san/三国志英杰传";
        UserSetting.rootOutputFolder = "D:/game/san/三国志英杰传/myex";
    	String[] fileNames = {"hexbchp.r3", "hexbchr.r3", "hexzchp.r3"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	Ls1112 ls1112File = new Ls1112(fileName);
        	ls1112File.type = 11;
        	ls1112File.gameName = Ls1112.GAME_NAME_YJZ;
        	ls1112File.decode();
        	ls1112File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
