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
import org.xpen.level5.layton.fileformat.CimgFile;
import org.xpen.level5.layton.fileformat.LimgHandler;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

/**
 * Professor Layton and the Unwound Future
 * 雷顿教授与最后的时间旅行
 * レイトン教授と最後の時間旅行
 * 1221/1445
 *
 */
public class Layton3Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton3Img.class);
    private static final String FILE_SUFFIX_CIMG = "cimg";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100437/root/lt3";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100437/root/lt3/myex";
        
        CimgFile cimgFile = new CimgFile();
        cimgFile.addFolderType("btl", new LimgHandler());
        cimgFile.addFolderType("chr", new LimgHandler());
        cimgFile.addFolderType("img", new LimgHandler());
        cimgFile.addFolderType("map", new LimgHandler());
        cimgFile.addFolderType("menu", new LimgHandler());
        cimgFile.addFolderType("mini", new LimgHandler());
        cimgFile.addFolderType("nazo", new LimgHandler());
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        
        Iterator<Entry<String, FileTypeHandler>> iterator = cimgFile.supporttedTypes.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Entry<String, FileTypeHandler> next = iterator.next();
            String folderName = next.getKey();
        	LOG.debug("---------Starting {}", folderName);
        	
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_CIMG}, true);
            for (File f : files) {
                totalCount++;
                try {
                    cimgFile.decode(folderName, f);
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
