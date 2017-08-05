package org.xpen.koei.sangokushi;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.koei.fileformat.Ls1112;
import org.xpen.util.UserSetting;

public class SanCaocaoZhuanE5 {
    
    private static final Logger LOG = LoggerFactory.getLogger(SanCaocaoZhuanE5.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/san/三国志曹操传";
        UserSetting.rootOutputFolder = "D:/game/san/三国志曹操传/myex";
    	String[] fileNames = {"Effarea.e5", "Face.e5", "Gate.e5", "Hitarea.e5"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	Ls1112 ls1112File = new Ls1112(fileName);
        	ls1112File.type = 12;
        	ls1112File.gameName = Ls1112.GAME_NAME_CCZ;
        	ls1112File.decode();
        	ls1112File.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
