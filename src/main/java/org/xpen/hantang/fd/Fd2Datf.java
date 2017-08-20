package org.xpen.hantang.fd;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.hantang.fd.fileformat.DatfFile;
import org.xpen.util.UserSetting;

public class Fd2Datf {
    
    private static final Logger LOG = LoggerFactory.getLogger(Fd2Datf.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2";
        UserSetting.rootOutputFolder = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex";
    	String[] fileNames = {"ANI", "BG", "DATO", "FDFIELD", "FDMUS", "FDOTHER", "FDSHAP", "FDTXT", "FIGANI",
    	        "TAI", "TITLE"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	DatfFile datfFile = new DatfFile(fileName);
        	datfFile.decode();
        	datfFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
