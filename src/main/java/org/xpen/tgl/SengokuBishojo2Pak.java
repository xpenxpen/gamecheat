package org.xpen.tgl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.tgl.format.PakFile;
import org.xpen.util.UserSetting;

/**
 * Sengoku Bishojo2
 * 战国美少女2:春风之章
 *
 */
public class SengokuBishojo2Pak {
    
    private static final Logger LOG = LoggerFactory.getLogger(SengokuBishojo2Pak.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/game/战国美少女2/DATA";
        UserSetting.rootOutputFolder = "D:/game/战国美少女2/DATA/myex";
        String[] fileNames = {"Grp"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);
        
            PakFile pakFile = new PakFile(fileName);
            pakFile.decode();
            pakFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
