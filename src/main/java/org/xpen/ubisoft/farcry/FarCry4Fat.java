package org.xpen.ubisoft.farcry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.DatFile;
import org.xpen.ubisoft.dunia2.fileformat.Fat2File;
import org.xpen.ubisoft.dunia2.fileformat.fat2.Entry;
import org.xpen.ubisoft.dunia2.fileformat.fat2.FileListManager;
import org.xpen.util.UserSetting;

public class FarCry4Fat {
    
    private static final Logger LOG = LoggerFactory.getLogger(FarCry4Fat.class);

    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "E:/aliBoxGames/games/11136/Far Cry 4/data_win32";
        //UserSetting.rootOutputFolder = "E:/aliBoxGames/games/11136/myex";
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/fc4dat";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/fc4dat/myex";
    	//String[] fileNames = {"common", "patch", "ige", "worlds/fcc_main/fcc_main"};
    	String[] fileNames = {"worlds/fcc_main/fcc_main"};
        
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
            flm.load(FarCry4Fat.class.getClassLoader().getResourceAsStream(
            		"farcry4/files/" + fileName + ".filelist"));
            Map<Long, String> crcMap = flm.getCrcMap();
            
            DatFile datFile = new DatFile(fileName, fat2File, flm);
            datFile.decode();
            datFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
