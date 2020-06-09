package org.xpen.capcom.aceattorney.gsj;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.capcom.aceattorney.gsj.fileformat.BinFile;
import org.xpen.util.UserSetting;

/**
 * 逆转裁判事典
 * 
 *
 */
public class GyakutenSaibanJitenUnpack {
    
    private static final Logger LOG = LoggerFactory.getLogger(GyakutenSaibanJitenUnpack.class);
    private static final String FILE_SUFFIX_BIN = "bin";
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8110105/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8110105/root/myex";
        String fileName = "data.bin";
        
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
