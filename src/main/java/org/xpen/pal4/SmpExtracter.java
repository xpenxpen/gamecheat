package org.xpen.pal4;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.SmpFile;
import org.xpen.util.UserSetting;

public class SmpExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(SmpExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/pal4/gamedata/Music";
        UserSetting.rootOutputFolder = "F:/game/pal4/myex/Music";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        String[] fileNames = {"p01-3.smp"};
        
        Collection<File> listFiles = FileUtils.listFiles(new File(UserSetting.rootInputFolder), new String[]{"smp"}, false);
        
        for (File file: listFiles) {
        //for (String fileName: fileNames) {
            String fileName = file.getName();
        	LOG.debug("---------Starting {}", fileName);
            
            SmpFile smpFile = new SmpFile(fileName);
            smpFile.decode();
            smpFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
