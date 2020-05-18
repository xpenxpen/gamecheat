package org.xpen.namco.conan.detectiveskilltrainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.ds.format.Narc;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Detective Conan: Detective Skill Trainer
 * 名侦探柯南 侦探力训练
 * 名探偵コナン 探偵力トレーナー
 * 47/155
 *
 */
public class ConanDetectiveSkillTrainer {
    private static final String FILE_SUFFIX_NARC = "narc";

    public static void main(String[] args) throws Exception {
        String rootInputFolder = "D:/soft/ga/nds/8100125/root";
        String rootOutputFolder1 = "D:/soft/ga/nds/8100125/root/myex";
        String rootOutputFolder2 = "D:/soft/ga/nds/8100125/root/myex2";
        
        String[] folderNames = {"balloon", "character", "common", "data_main", "ending",
                "first_play", "guide", "main_menu", "rival", "sokutei", "story", "title",
                "training", "tutorial"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        //Step 1
        UserSetting.rootInputFolder = rootInputFolder;
        UserSetting.rootOutputFolder = rootOutputFolder1;
        
        for (String folderName: folderNames) {
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_NARC}, false);
            for (File f : files) {
                Path path = f.toPath();
                byte[] bytes = Files.readAllBytes(path);
                Narc narc = new Narc();
                narc.handle(bytes, folderName);
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
