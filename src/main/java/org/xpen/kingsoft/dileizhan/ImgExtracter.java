package org.xpen.kingsoft.dileizhan;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.kingsoft.fileformat.ImgFile;
import org.xpen.util.UserSetting;

public class ImgExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImgExtracter.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/抗日地雷战";
        UserSetting.rootOutputFolder = "F:/game/抗日地雷战/myex";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Collection<File> listFiles = FileUtils.listFiles(new File(UserSetting.rootInputFolder), new String[]{"IMG"}, false);
        
        for (File file: listFiles) {
            String fileName = file.getName();
        	LOG.debug("---------Starting {}", fileName);
            
            ImgFile imgFile = new ImgFile(fileName);
            imgFile.decode();
            imgFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
