package org.xpen.atlus.noora;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.ds.format.Narc;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.NintendoLz10Compressor;

/**
 * Noora Koubou
 * 诺拉和时间工房
 * 998/1591
 *
 */
public class NooraKoubouNbgcr {
    private static final Logger LOG = LoggerFactory.getLogger(NooraKoubouNbgcr.class);
    
    private static final String FILE_SUFFIX_NBGCR = "nbgcr";
    private static final String FILE_SUFFIX_NSPCR = "nspcr";

    public static void main(String[] args) throws Exception {
        String rootInputFolder = "D:/soft/ga/nds/8100387/root";
        String rootOutputFolder1 = "D:/soft/ga/nds/8100387/root/myex";
        String rootOutputFolder2 = "D:/soft/ga/nds/8100387/root/myex2";
        
        //String[] folderNames = {"abc"};
        String[] folderNames = {"cmn", "scn", "tst"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        //Step 1
        UserSetting.rootInputFolder = rootInputFolder;
        UserSetting.rootOutputFolder = rootOutputFolder1;
        
        for (String folderName: folderNames) {
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_NBGCR, FILE_SUFFIX_NSPCR}, true);
            for (File f : files) {
                Path path = f.toPath();
                byte[] bytes = Files.readAllBytes(path);
                bytes = Arrays.copyOfRange(bytes, 8, bytes.length);
                byte[] decompressBytes;
                try {
                    decompressBytes = NintendoLz10Compressor.decompress(bytes);
                } catch (Exception e) {
                    LOG.warn("Decompress error: " + f);
                    continue;
                }
                
                Path rootInputPath = Paths.get(rootInputFolder);
                Path relativize = rootInputPath.relativize(path.getParent());
                
                
                Narc narc = new Narc();
                narc.handle(decompressBytes, relativize, folderName);
            }
        }
        
        //Step 2
        UserSetting.rootInputFolder = rootOutputFolder1;
        UserSetting.rootOutputFolder = rootOutputFolder2;
        
        NclrNcgrNscr.extractNclrNcgrNscr(folderNames, countPair, null);
        NclrNcbrNcer.extractNclrNcbrNcer(folderNames, countPair, null);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
