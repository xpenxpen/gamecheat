package org.xpen.level5.layton;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.level5.layton.fileformat.AniHandler;
import org.xpen.level5.layton.fileformat.ArcFile;
import org.xpen.level5.layton.fileformat.BgHandler;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

/**
 * Professor Layton and the Curious Village
 * 雷顿教授与不可思议的小镇
 * レイトン教授と不思議な町
 * 1627/1661
 *
 */
public class Layton1Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton1Img.class);
    private static final String FILE_SUFFIX_ARC = "arc";
    private static final String FILE_SUFFIX_BGX = "bgx";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100435/root/data";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100435/root/myex";
    	
    	ArcFile arcFile = new ArcFile();
        arcFile.addFolderType("ani", new AniHandler());
        arcFile.addFolderType("bg", new BgHandler());
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        
        Iterator<Entry<String, FileTypeHandler>> iterator = arcFile.supporttedTypes.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Entry<String, FileTypeHandler> next = iterator.next();
            String folderName = next.getKey();
        	LOG.debug("---------Starting {}", folderName);
        	
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_ARC}, false);
            for (File f : files) {
                totalCount++;
                try {
                    arcFile.decode(folderName, f);
                    handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip {}", f.getName());
                }
            }
        	
            
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);

    }

}
