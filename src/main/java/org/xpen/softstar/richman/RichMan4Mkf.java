package org.xpen.softstar.richman;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.softstar.richman.fileformat.MkfFile;
import org.xpen.util.UserSetting;

/**
 * Richman 4
 * 大富翁4
 *
 */
public class RichMan4Mkf {
    
    private static final Logger LOG = LoggerFactory.getLogger(RichMan4Mkf.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/QQGameDownload/PCGames/QGRichman4";
        UserSetting.rootOutputFolder = "E:/QQGameDownload/PCGames/QGRichman4/myex";
        String[] fileNames = {"Speaking"};
        //String[] fileNames = {"jump"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
        	MkfFile mkfFile = new MkfFile(fileName);
        	mkfFile.decode();
        	mkfFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
