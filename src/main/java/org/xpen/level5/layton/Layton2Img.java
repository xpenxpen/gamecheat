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
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

/**
 * Professor Layton and the Diabolical Box
 * 雷顿教授与恶魔之箱
 * レイトン教授と悪魔の箱
 * 1747/1806
 *
 */
public class Layton2Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton2Img.class);
    private static final String FILE_SUFFIX_ARC = "arc";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100436/root/data_lt2";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100436/root/data_lt2/myex";
        
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
