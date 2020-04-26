package org.xpen.level5.layton;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.level5.layton.fileformat.FaFile;
import org.xpen.util.UserSetting;

/**
 * Professor Layton and the Last Specter
 * 雷顿教授与魔神之笛
 * レイトン教授と魔神の笛
 * 0/0
 *
 */
public class Layton4Fa {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton3Img.class);
    private static final String FILE_SUFFIX_CIMG = "cimg";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100438/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100438/root/myex";
        
        String[] fileNames = {"lt4_main"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);
            
            FaFile file = new FaFile(fileName);
            file.decode();
            file.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        //System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);

    }

}
