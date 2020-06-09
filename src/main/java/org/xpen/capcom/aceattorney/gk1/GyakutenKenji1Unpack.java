package org.xpen.capcom.aceattorney.gk1;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.capcom.aceattorney.gk1.fileformat.BinFile;
import org.xpen.util.UserSetting;

/**
 * Ace Attorney Investigations: Miles Edgeworth
 * 逆转检事1
 * 逆転検事1
 * 
 *
 */
public class GyakutenKenji1Unpack {
    
    private static final Logger LOG = LoggerFactory.getLogger(GyakutenKenji1Unpack.class);
    private static final String FILE_SUFFIX_BIN = "bin";
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex";
        //UserSetting.rootInputFolder = "D:/soft/ga/nds/8003809/root/files";
        //UserSetting.rootOutputFolder = "D:/soft/ga/nds/8003809/root/files/myex";
        String fileName = "romfile.bin";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
            
        Path path = Paths.get(UserSetting.rootInputFolder, fileName);
        File f = path.toFile();
        String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
        BinFile binFile = new BinFile();
        //try {
            binFile.decode(oldFileNameWithoutExt, path.toFile());
        //} catch (Exception e) {
        //    LOG.warn("Error occurred, skip {}", f.getName());
        //} finally {
            binFile.close();
        //}
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);
    }

}
