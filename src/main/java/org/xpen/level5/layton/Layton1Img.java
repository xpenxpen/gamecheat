package org.xpen.level5.layton;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.level5.layton.fileformat.AniFile;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.Lz10Compressor;

/**
 * Professor Layton 1: Curious Village
 * 雷顿教授1：不可思议的小镇
 *
 */
public class Layton1Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(Layton1Img.class);
    private static final String FILE_SUFFIX_ARC = "arc";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/game/nds/8100435/root/data";
        UserSetting.rootOutputFolder = "D:/soft/game/nds/8100435/root/myex";
    	String[] folderNames = {"ani"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        
        for (String folderName: folderNames) {
        	LOG.debug("---------Starting {}", folderName);
        	
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_ARC}, false);
            for (File f : files) {
                totalCount++;
                try {
                    //System.out.println("Start "+ f.getName());
                    byte[] inBytes = FileUtils.readFileToByteArray(f);
                    
                    //CAN only handle compress type 02 now
                    if (inBytes[0] != 2 || inBytes[1] != 0 || inBytes[2] != 0 || inBytes[3] != 0) {
                        continue;
                    }
                    inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
                    
                    byte[] outBytes = Lz10Compressor.decompress(inBytes);
                    
                    //FileUtils.writeByteArrayToFile(new File("D:/git/opensource/gamecheat/1.dat"), outBytes);
                    //break;
                    
                    AniFile aniFile = new AniFile(folderName,
                            f.getName().substring(0, f.getName().lastIndexOf(".")), outBytes);
                    aniFile.decode();
                    aniFile.close();
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
