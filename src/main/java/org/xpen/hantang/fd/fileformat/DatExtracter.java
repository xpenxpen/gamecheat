package org.xpen.hantang.fd.fileformat;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class DatExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2";
        UserSetting.rootOutputFolder = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex";
    	String[] fileNames = {"ANI", "BG", "DATO", "FDFIELD", "FDMUS", "FDOTHER", "FDSHAP", "FDTXT", "FIGANI",
    	        "PASS", "TAI", "TITLE"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	DatfFile mkfFile = new DatfFile(fileName);
        	mkfFile.decode();
        	mkfFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
