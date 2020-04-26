package org.xpen.ubisoft.farcry;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.cryengine.fileformat.PakFile;
import org.xpen.util.UserSetting;

public class FarCry1Pak {
    
    private static final Logger LOG = LoggerFactory.getLogger(FarCry1Pak.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/aliBoxGames/games/3641/Far Cry/FCData";
        UserSetting.rootOutputFolder = "E:/aliBoxGames/games/3641/Far Cry/FCData/myex";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Collection<File> listFiles = FileUtils.listFiles(new File(UserSetting.rootInputFolder), new String[]{"pak", "PAK"}, false);
        Collection<File> listFiles2 = FileUtils.listFiles(new File(UserSetting.rootInputFolder, "Localized"), new String[]{"pak"}, false);

        for (File file: listFiles) {
            String fileName = file.getName();
        	LOG.debug("---------Starting {}", fileName);
            
            PakFile pakFile = new PakFile(fileName);
            pakFile.decode();
            pakFile.close();
        }

        for (File file: listFiles2) {
            String fileName = file.getName();
        	LOG.debug("---------Starting {}", fileName);
            
            PakFile pakFile = new PakFile("Localized/" + fileName);
            pakFile.decode();
            pakFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
