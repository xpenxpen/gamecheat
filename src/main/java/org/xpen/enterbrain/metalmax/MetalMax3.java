package org.xpen.enterbrain.metalmax;

import java.io.File;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.ds.format.FormatConstants;
import org.xpen.enterbrain.metalmax.fileformat.PakFile;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Metal Max 3
 * 重装机兵3
 * メタルマックス3
 * 0/0
 *
 */
public class MetalMax3 {
    
    private static final Logger LOG = LoggerFactory.getLogger(MetalMax3.class);
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/metalmax/mm3/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/metalmax/mm3/root/myex";
        String[] folderNames = {"Battle", "Char", "Menu", "Mini", "Tank", "World", "xls"};
        //String[] folderNames = {"Mini"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        extractPak(folderNames, countPair, null);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

    private static void extractPak(String[] folderNames, HandleCount countPair, Object object) {
        for (String folderName: folderNames) {
            LOG.debug("---------Starting {}", folderName);
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{"pak"}, true);
            for (File f : files) {
                countPair.totalCount++;
                try {
                    
                    String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
                    File level1Parent = f.getParentFile();
                    File parent = f.getParentFile();
                    Stack<String> folderNameStack = new Stack<>();
                    while (!parent.getName().equals(folderName)) {
                        folderNameStack.push(parent.getName());
                        parent = parent.getParentFile();
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(folderName);
                    while (!folderNameStack.isEmpty()) {
                        sb.append("/");
                        sb.append(folderNameStack.pop());
                    }
                    
                    PakFile pak = new PakFile(sb.toString(), oldFileNameWithoutExt);
                    pak.decode();
                    pak.close();
                    
                    countPair.handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip " + f.getName(), e);
                }
            }
        }
        
    }

}
