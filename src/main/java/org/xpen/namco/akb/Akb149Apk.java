package org.xpen.namco.akb;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.namco.fileformat.ApkFile;
import org.xpen.namco.fileformat.IdxFile;
import org.xpen.util.UserSetting;

public class Akb149Apk {
    
    private static final Logger LOG = LoggerFactory.getLogger(Akb149Apk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/psp/akb149/NoLabel/PSP_GAME/USRDIR/dev/ms0";
        UserSetting.rootOutputFolder = "D:/psp/akb149/NoLabel/PSP_GAME/USRDIR/dev/ms0/myex";
    	String[] idxFileNames = {"PACK"};
    	String[] apkFileNames = {"ALLD2"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (int i = 0; i < idxFileNames.length; i++) {
        	String idxFileName = idxFileNames[i];
        	String apkFileName = apkFileNames[i];
        	LOG.debug("---------Starting {}", apkFileName);
            
        	IdxFile idxFile = new IdxFile(idxFileName);
        	idxFile.decode();
        	idxFile.close();
        	
        	ApkFile apkFile = new ApkFile(apkFileName);
        	apkFile.setFatEntries(idxFile.getFatEntries());
        	apkFile.decode();
        	apkFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
