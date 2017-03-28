package org.xpen.farcry3;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.DatFile;
import org.xpen.dunia2.fileformat.Fat2File;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.FileListManager;

public class FatExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(FatExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/aliBoxGames/games/5993/FarCry 3/data_win32";
        UserSetting.rootOutputFolder = "E:/aliBoxGames/games/5993/myex";
    	//String[] fileNames = {"common", "patch", "igepatch", "ige", "worlds/fc3main/fc3_main"};
    	String[] fileNames = {"worlds/fc3_main/fc3_main"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            Fat2File fat2File = new Fat2File(fileName);
            fat2File.decode();
            fat2File.close();
            
            List<Entry> entries = fat2File.getEntries();
            if (LOG.isDebugEnabled()) {
    	        for (Entry entry : entries) {
    	            LOG.debug(entry.toString());
    	        }
            }
            
            FileListManager flm = new FileListManager();
            flm.load(FatExtracter.class.getClassLoader().getResourceAsStream(
            		"farcry3/files/" + fileName + ".filelist"));
            Map<Long, String> crcMap = flm.getCrcMap();
            
            DatFile datFile = new DatFile(fileName, fat2File, flm);
            datFile.decode();
            datFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
