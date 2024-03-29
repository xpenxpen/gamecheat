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
 * Professor Layton and the Holiday of London
 * 雷顿教授与伦敦的休日
 * レイトン教授とロンドンの休日
 * 342/352
 *
 */
public class Layton2LondonLifeImg {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton2LondonLifeImg.class);
    private static final String FILE_SUFFIX_ARC = "arc";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100436/root/data_lt1";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100436/root/data_lt1/myex";
        
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
                    new String[]{FILE_SUFFIX_ARC}, true);
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
